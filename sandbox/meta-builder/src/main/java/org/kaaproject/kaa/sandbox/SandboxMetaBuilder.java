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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SandboxMetaBuilder implements SandboxConstants {

    private OsType osType = OsType.DEBIAN;

    private static final Logger LOG = LoggerFactory.getLogger(SandboxMetaBuilder.class);

    private String command;


    private String[] sandboxServices = {"zookeeper", "postgresql", "mongodb"};//  , "kaa_box_startup"};

    public static void main(String[] args) throws Exception {
        SandboxMetaBuilder builder = new SandboxMetaBuilder(args[0], Integer.valueOf(args[1]), args[2], args[3]);
        builder.buildSandboxMeta();
    }


    //    private String host;
    private int webAdminForwardPort;
//    private String projectsXmlFileLocation;


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

    SandboxMetaBuilder(String host, int webAdminForwardPort, String projectsXmlFileLocation, String command) {
//        this.host = host;
        this.webAdminForwardPort = webAdminForwardPort;
//        this.projectsXmlFileLocation = projectsXmlFileLocation ;
        this.command = command;
    }


    public void buildSandboxMeta() throws Exception {
        List<String> cmds = prepareServicesStartCommands();
        for (String cmd : cmds) {
            LOG.info(cmd);
            executeCommand(cmd);
            Thread.sleep(3_000);
        }

        LOG.info("WAITING FOR SERVICES START");
        Thread.sleep(50_000);

        List<Project> projects = new ArrayList<>();
        AdminClient adminClient = new AdminClient(DEFAULT_HOST, webAdminForwardPort);
        List<DemoBuilder> demoBuilders = DemoBuildersRegistry.getRegisteredDemoBuilders();
        for (DemoBuilder demoBuilder : demoBuilders) {
            try {
                demoBuilder.buildDemoApplication(adminClient);
                projects.addAll(demoBuilder.getProjectConfigs());
            }catch (Exception e){
                LOG.info(e.getMessage());
            }
        }

        prepareProjectsXmlFile(projects, SANDBOX_FOLDER + "/" + DEMO_PROJECTS + "/" + DEMO_PROJECTS_XML);
        LOG.info("PROJECTS XML PREPARED");


        executeCommand("sudo service kaa-admin restart");
        LOG.info("WAITING 50 SEC FOR KAA ADMIN RESTART");
        Thread.sleep(50000);

        LOG.info("Building demo applications...");
        SandboxClient sandboxClient = new SandboxClient(DEFAULT_HOST, webAdminForwardPort);


        List<Project> sandboxProjects = sandboxClient.getDemoProjects();
        if (projects.size() != sandboxProjects.size()) {
            LOG.error("Demo projects count mismatch, expected {}, actual {}", projects.size(), sandboxProjects.size());
            throw new RuntimeException("Demo projects count mismatch!");
        }
        for (Project sandboxProject : sandboxProjects) {
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

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

}
