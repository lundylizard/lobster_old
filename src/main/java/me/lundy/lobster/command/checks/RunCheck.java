package me.lundy.lobster.command.checks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RunCheck {
    CommandCheck check();
}
