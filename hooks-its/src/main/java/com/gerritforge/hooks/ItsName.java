package com.gerritforge.hooks;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.google.inject.BindingAnnotation;

@Retention(RUNTIME)
@BindingAnnotation
public @interface ItsName {
}
