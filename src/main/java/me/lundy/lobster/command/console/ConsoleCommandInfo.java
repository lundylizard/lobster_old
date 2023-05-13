package me.lundy.lobster.command.console;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleCommandInfo {
    String name();
    String description();
}
