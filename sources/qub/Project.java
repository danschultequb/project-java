package qub;

public interface Project
{
    String projectFolderParameterName = "projectFolder";

    static void main(String[] args)
    {
        DesktopProcess.run(args, Project::run);
    }

    static CommandLineActions createCommandLineActions(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        return process.createCommandLineActions()
            .setApplicationName("qub-project")
            .setApplicationDescription("An application used to interact with projects.");
    }

    static void run(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        Project.createCommandLineActions(process)
            .addAction(ProjectRun::addAction)
            .addAction(CommandLineLogsAction::addAction)
            .addAction(CommandLineConfigurationAction::addAction)
            .run();
    }

    static CommandLineParameter<Folder> addProjectFolderParameter(CommandLineParameters parameters, DesktopProcess process, String parameterDescription)
    {
        PreCondition.assertNotNull(parameters, "parameters");
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNullAndNotEmpty(parameterDescription, "parameterDescription");

        return parameters.addFolder(Project.projectFolderParameterName, process)
            .setDescription(parameterDescription);
    }

    static CommandLineParameterProfiler addProfilerParameter(CommandLineParameters parameters, DesktopProcess process)
    {
        PreCondition.assertNotNull(parameters, "parameters");
        PreCondition.assertNotNull(process, "process");

        return parameters.addProfiler(process, Project.class);
    }
}
