package com.denzo.daschess.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Scope
@Retention(RUNTIME)
public @interface ActivityScope {
}
