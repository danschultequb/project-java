package qub;

public interface ProjectRunTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(ProjectRun.class, () ->
        {
            runner.testGroup("addAction(CommandLineActions)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> ProjectRun.addAction(null),
                        new PreConditionFailure("actions cannot be null."));
                });

                runner.test("with non-null",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineActions actions = Project.createCommandLineActions(process);

                    final CommandLineAction action = ProjectRun.addAction(actions);
                    test.assertNotNull(action);
                    test.assertEqual("run", action.getName());
                    test.assertEqual("qub-project [run]", action.getFullName());
                    test.assertEqual("Run the provided project action.", action.getDescription());
                    test.assertEqual(Iterable.create(), action.getAliases());
                    test.assertTrue(action.isDefaultAction());
                    test.assertSame(process, action.getProcess());

                    test.assertTrue(actions.containsActionName(action.getName()));
                });
            });

            runner.testGroup("run(DesktopProcess,CommandLineAction)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> ProjectRun.run(null, CommandLineAction.create("fake-action-name", (DesktopProcess process) -> {})),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with null action",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    test.assertThrows(() -> ProjectRun.run(process, null),
                        new PreConditionFailure("action cannot be null."));
                });

                runner.test("with " + Strings.escapeAndQuote("-?"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("-?")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = ProjectRunTests.createAction(process);

                    ProjectRun.run(process, action);

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

                runner.test("with " + Strings.escapeAndQuote(""),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = ProjectRunTests.createAction(process);

                    ProjectRun.run(process, action);

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

                runner.test("with " + Strings.escapeAndQuote("spam"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("spam")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = ProjectRunTests.createAction(process);

                    ProjectRun.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Running \"spam\" in \"/\"..."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());
                });

                runner.test("with " + Strings.escapeAndQuote("spam a b c"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("spam", "a", "b", "c")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = ProjectRunTests.createAction(process);

                    ProjectRun.run(process, action);

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

    static CommandLineAction createAction(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineActions actions = Project.createCommandLineActions(process);
        return ProjectRun.addAction(actions);
    }
}
