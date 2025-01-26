package com.suntrustbank.user.core.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * when {@code @AuthorizedAdminUser} is present it validates the Admin and Role of the Admin
 * accessing a particular endpoint
 * <p>
 * {@code hasAuthority()}, This specifies the claim
 * <p>
 */
@Documented
@Target({METHOD})
@Retention(RUNTIME)

public @interface AuthorizedAdminUser {
    String message() default "access.denied";

    String hasAuthority();
}
