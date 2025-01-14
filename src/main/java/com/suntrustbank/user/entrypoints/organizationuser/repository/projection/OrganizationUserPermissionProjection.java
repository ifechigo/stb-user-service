package com.suntrustbank.user.entrypoints.organizationuser.repository.projection;

import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;

public interface OrganizationUserPermissionProjection {
    String getUserReference();
    String getEmail();
    Long getAccountId();
    String getOrganizationReference();
    Long getOrganizationId();
    OrganizationRole getRole();
    boolean getStatus();
    Status getAccountOrgStatus();
    boolean getAccountOrgActive();
    String getOrganizationName();
    String getPermissions();
}
