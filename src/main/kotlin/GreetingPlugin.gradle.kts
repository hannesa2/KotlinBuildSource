
/*
plugins {
    id("GreetingPlugin")
}
*/
tasks.register("changelog") {
    doLast {
        println("Hello from the convention ChangelogPlugin https://docs.gradle.org/current/userguide/plugins.html#sec:precompile_script_plugin")
    }
}