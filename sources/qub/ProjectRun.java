package qub;

public interface ProjectRun
{
    static CommandLineAction addAction(CommandLineActions actions)
    {
        PreCondition.assertNotNull(actions, "actions");

        return actions.addAction("run", ProjectRun::run)
            .setDescription("Run the provided project action.")
            .setDefaultAction();
    }

    static void run(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters();
        final CommandLineParameter<String> projectActionNameParameter = parameters.addPositionString("projectAction")
            .setDescription("The project action that will be run.")
            .setRequired(true);
        final CommandLineParameter<Folder> projectFolderParameter = Project.addProjectFolderParameter(parameters, process,
            "The folder that contains the project to run an action in. Defaults to the current folder.");
        final CommandLineParameterList<String> additionalArgumentsParameter = parameters.addPositionStringList("additionalArguments")
            .setDescription("Additional arguments that will be passed down to the project-specific action.");
        final CommandLineParameterHelp helpParameter = parameters.addHelp();
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);
        final CommandLineParameterProfiler profilerParameter = Project.addProfilerParameter(parameters, process);

        final String projectActionName = projectActionNameParameter.getValue().await();
        helpParameter.setForceShowApplicationHelpLines(Strings.isNullOrEmpty(projectActionName));

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            profilerParameter.await();

            final LogStreams logStreams = CommandLineLogsAction.getLogStreamsFromDesktopProcess(process, verboseParameter.getVerboseCharacterToByteWriteStream().await());
            try (final Disposable logStream = logStreams.getLogStream())
            {
                final CharacterToByteWriteStream output = logStreams.getOutput();

                final Folder projectFolder = projectFolderParameter.getValue().await();
                if (!projectFolder.exists().await())
                {
                    output.writeLine("Project folder " + Strings.escapeAndQuote(projectFolder.toString()) + " doesn't exist.").await();
                    process.setExitCode(-1);
                }
                else
                {
                    output.writeLine("Running " + Strings.escapeAndQuote(projectActionName) + " in " + Strings.escapeAndQuote(projectFolder.toString()) + "...").await();
                }
            }
        }
    }
}
