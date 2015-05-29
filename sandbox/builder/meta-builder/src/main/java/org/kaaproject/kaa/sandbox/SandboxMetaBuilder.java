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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SandboxMetaBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SandboxMetaBuilder.class);


    public static void main(String[] args) throws Exception {
        SandboxMetaBuilder builder = new SandboxMetaBuilder(args[0],Integer.valueOf(args[1]),args[2]);
        builder.buildSandboxMeta();
    }

//    SANDBOX_FOLDER + "/" + DEMO_PROJECTS +"/"+ DEMO_PROJECTS_XML


    private String host;
    private int webAdminForwardPort;
    private String projectsXmlFileLocation;

    SandboxMetaBuilder (String host, int webAdminForwardPort, String projectsXmlFileLocation){
        this.host = host;
        this.webAdminForwardPort =  webAdminForwardPort;
        this.projectsXmlFileLocation = projectsXmlFileLocation ;
    }


    public void buildSandboxMeta() throws Exception {

        List<Project> projects = new ArrayList<>();
        AdminClient adminClient = new AdminClient(host, webAdminForwardPort);
        List<DemoBuilder> demoBuilders = DemoBuildersRegistry.getRegisteredDemoBuilders();
        for (DemoBuilder demoBuilder : demoBuilders) {
            demoBuilder.buildDemoApplication(adminClient);
            projects.addAll(demoBuilder.getProjectConfigs());
        }

        prepareProjectsXmlFile(projects, projectsXmlFileLocation);

        LOG.info("Building demo applications...");
        SandboxClient sandboxClient = new SandboxClient(host, webAdminForwardPort);


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


}
