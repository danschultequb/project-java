package qub;

public interface ProjectTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Project.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Project.main(null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("createCommandLineActions(DesktopProcess)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Project.createCommandLineActions(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with non-null",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                    {
                        final CommandLineActions actions = Project.createCommandLineActions(process);
                        test.assertNotNull(actions);
                        test.assertEqual("qub-project", actions.getApplicationName());
                        test.assertEqual("An application used to interact with projects.", actions.getApplicationDescription());
                    });
            });

            runner.testGroup("run(DesktopProcess)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Project.run(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with \"-?\"",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("-?")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    Project.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Usage: qub-project [--action=]<action-name> [--help]",
                            "  An application used to interact with projects.",
                            "  --action(a): The name of the action to invoke.",
                            "  --help(?):   Show the help message for this application.",
                            "",
                            "Actions:",
                            "  configuration: Open the configuration file for this application.",
                            "  logs:          Show the logs folder.",
                            "  run (default): Run the provided project action."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(-1, process.getExitCode());
                });

                runner.test("with no arguments",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    Project.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Usage: qub-project [run] [--projectAction=]<projectAction-value> [[--additionalArguments=]<additionalArguments-value>] [--projectFolder=<projectFolder-value>] [--help] [--verbose] [--profiler]",
                            "  Run the provided project action.",
                            "  --projectAction:       The project action that will be run.",
                            "  --additionalArguments: Additional arguments that will be passed down to the project-specific action.",
                            "  --projectFolder:       The folder that contains the project to run an action in. Defaults to the current folder.",
                            "  --help(?):             Show the help message for this application.",
                            "  --verbose(v):          Whether or not to show verbose logs.",
                            "  --profiler:            Whether or not this application should pause before it is run to allow a profiler to be attached."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(-1, process.getExitCode());
                });

                runner.test("with \"spam\"",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("spam")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    Project.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Running \"spam\" in \"/\"..."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());
                });
            });
        });
    }
}
