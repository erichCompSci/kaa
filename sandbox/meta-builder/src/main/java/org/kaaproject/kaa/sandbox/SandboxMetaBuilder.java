package org.kaaproject.kaa.sandbox;

import org.kaaproject.kaa.sandbox.demo.DemoBuilder;
import org.kaaproject.kaa.sandbox.demo.DemoBuildersRegistry;
import org.kaaproject.kaa.sandbox.demo.projects.Project;
import org.kaaproject.kaa.sandbox.demo.projects.ProjectsConfig;
import org.kaaproject.kaa.sandbox.rest.SandboxClient;
import org.kaaproject.kaa.server.common.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SandboxMetaBuilder implements SandboxConstants {

    private static final Logger LOG = LoggerFactory.getLogger(SandboxMetaBuilder.class);

    private OsType osType = OsType.DEBIAN;

    private String[] sandboxServices = {"zookeeper", "postgresql", "mongod"};


    SandboxMetaBuilder(int webAdminForwardPort) {
        this.webAdminForwardPort = webAdminForwardPort;
    }

    private int webAdminForwardPort;

    protected List<String> prepareServicesStartCommands() {
        LinkedList<String> servicesStartCommands = new LinkedList<>();
        for (String serviceName : sandboxServices) {
            servicesStartCommands.add(osType.getStartServiceTemplate().
                    replaceAll(SERVICE_NAME_VAR, serviceName));
        }
        for (KaaPackage kaaPackage : KaaPackage.values()) {
            servicesStartCommands.add(osType.getStartServiceTemplate().
                    replaceAll(SERVICE_NAME_VAR, kaaPackage.getServiceName()));
        }
        return servicesStartCommands;
    }


    public void buildSandboxMeta() throws Exception {
        List<String> cmds = prepareServicesStartCommands();
        for (String cmd : cmds) {
            LOG.info("Executing [{}]",cmd);
            executeCommand(cmd);
        }

        LOG.info("Waiting for services to start");
        waitForServices();

        List<Project> projects = new ArrayList<>();
        AdminClient adminClient = new AdminClient(DEFAULT_HOST, webAdminForwardPort);
        List<DemoBuilder> demoBuilders = DemoBuildersRegistry.getRegisteredDemoBuilders();
        for (DemoBuilder demoBuilder : demoBuilders) {
            try {
                demoBuilder.buildDemoApplication(adminClient);
                projects.addAll(demoBuilder.getProjectConfigs());
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }
        }

        prepareProjectsXmlFile(projects, SANDBOX_FOLDER + "/" + DEMO_PROJECTS + "/" + DEMO_PROJECTS_XML);
        LOG.info("PROJECTS XML PREPARED");

        String adminRestartCommand = osType.getStartServiceTemplate().
                replaceAll(SERVICE_NAME_VAR, KaaPackage.ADMIN.getServiceName()).replaceAll("start", "restart");
        executeCommand(adminRestartCommand);

        LOG.info("Waiting for admin restart");

        waitForServices();


        LOG.info("Building demo applications...");
        SandboxClient sandboxClient = new SandboxClient(DEFAULT_HOST, webAdminForwardPort);


        List<Project> sandboxProjects = sandboxClient.getDemoProjects();
        if (projects.size() != sandboxProjects.size()) {
            LOG.error("Demo projects count mismatch, expected {}, actual {}", projects.size(), sandboxProjects.size());
            throw new RuntimeException("Demo projects count mismatch!");
        }
        for (Project sandboxProject : sandboxProjects) {
            try {
                if (sandboxProject.getDestBinaryFile() != null &&
                        sandboxProject.getDestBinaryFile().length() > 0) {
                    LOG.info("[{}][{}] Building Demo Project...", sandboxProject.getPlatform(), sandboxProject.getName());
                    String output = sandboxClient.buildProjectBinary(sandboxProject.getId());
                    LOG.info("[{}][{}] Build output:\n{}", sandboxProject.getPlatform(), sandboxProject.getName(), output);
                    if (!sandboxClient.isProjectBinaryDataExists(sandboxProject.getId())) {
                        LOG.error("Failed to build demo project '{}'", sandboxProject.getName());
                        throw new RuntimeException("Failed to build demo project '" + sandboxProject.getName() + "'!");
                    }
                } else {
                    LOG.info("[{}][{}] Skipping Demo Project build...", sandboxProject.getPlatform(), sandboxProject.getName());
                }
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }
        }
        LOG.info("Finished building demo applications!");
    }

    private File prepareProjectsXmlFile(List<Project> projects, String projectsXmlFileLocation) throws JAXBException {
        File projectsXmlFile = new File(projectsXmlFileLocation);
        ProjectsConfig projectsConfig = new ProjectsConfig();
        projectsConfig.getProjects().addAll(projects);

        JAXBContext jc = JAXBContext.newInstance("org.kaaproject.kaa.sandbox.demo.projects");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(projectsConfig, projectsXmlFile);
        return projectsXmlFile;
    }


    private String executeCommand(String command) {

        StringBuilder output = new StringBuilder();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return output.toString();

    }


    private int executeCommandAndGetExitValue(String command) throws InterruptedException, IOException {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        return p.exitValue();
    }


    private void waitForServices() throws InterruptedException, IOException {
            while (true) {
                int exitValue = 0;
                exitValue = executeCommandAndGetExitValue("wget -P /tmp 127.0.0.1:" + webAdminForwardPort);
                if (exitValue == 0) {
                    break;
                } else {
                    LOG.info("Services not ready. waiting....", exitValue);
                    Thread.sleep(3_000);
                }
            }
    }


    public static void main(String[] args) throws Exception {
        SandboxMetaBuilder builder = new SandboxMetaBuilder(Integer.valueOf(args[0]));
        builder.buildSandboxMeta();
    }


}
