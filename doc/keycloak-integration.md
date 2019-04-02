# Enabling Oauth 2.0 Security with Keycloak

# Roles

The document service is currently enforcing the following roles:

* DOKUTI_USER
* DOKUTI_ADMIN

The application is expecting these roles to be within the Oauth 2.0 JWT.

# Keycloak Integration

Keycloak is supported as an Oauth 2.0 provider for the document service. In order to use Keycloak for this purpose, an Oauth 2.0 'dokuti' client needs
to be configured for the active Keycloak Realm. The following sub-sections detail how to configure Keycloak for use with the document service.

## Create 'dokuti' Client

Use the Keycloak UI to create a new client in the active Realm (This is usually Dokuti). Set the following values in the form:

Keycloak Breadcrumb: ```Clients```

```
Client ID       : dokuti
Client Protocol : openid-connect
```

## Configure Client

Once the client has been created it needs some specific configurations. Please ensure the following configurations are applied:

Keycloak Breadcrumb: ```Clients >> dokuti```


```
Access Type: confidential
Valid Redirect URIs : http(s)://<service-hostname>:<service-port>. Example. http://dokuti.org
Root URL: http(s)://<service-hostname>:<service-port>. Example. http://dokuti.org
Standard Flow Enabled: On.
Implicit Flow Enabled: Off.
Direct Access Grants Enabled: On.
# Required for micro-service to micro-service secured calls
Service Accounts Enabled: On
Authorization Enabled : On
```

## Create the Required Client Roles:

Keycloak Breadcrumb: ```Clients >> dokuti >> Roles``` 

Create the Following Client Roles for the 'dokuti' client. All the Roles specified in the Roles section of this document must be created as client Roles
within Keycloak.

* DOKUTI_USER
* DOKUTI_ADMIN

## Create a Mappers for Token

### Create Username Mapper

The Keycloak access token is a JWT. It is a JSON payload and each field in that JSON is called a claim. By default, logged in username is returned in a claim named “preferred_username” in access token. 
Spring Security OAuth2 Resource Server expects username in a claim named “user_name”. Hence, a claim mapper needs to to map logged in "username" to a new claim named "user_name".

Keycloak Breadcrumb: ```Clients >> dokuti >> Mappers``` 

Use the 'Create' button to add a new mapper. Call the mapper Username.

Once the mapper is created, select it from the list and configure the following:

Keycloak Breadcrumb: ```Clients >> dokuti >> Mappers >> Username``` 

```
Name: Username (Should be set after the creation)
Mapper Type: User Property
Property: username
Token Claim Name: user_name
Claim JSON Type: String
Add to ID token: On
Add to access token: On
Add to userinfo: On
```

## Create a User

Create a new user in Keycloak by using the User tab. 

Keycloak Breadcrumb: ```Users```

## Map Client Roles to User

In order to enable the newly created user to access Dokuti we need to map the required roles onto the User.

Keycloak Breadcrumb: ```Users >> username-you-created```

Click the "Role Mappings" tab.

Select "dokuti" from the "Client Roles" dropdown. This should update the screen to display "Available Roles", "Assigned Roles", and "Effective Roles" boxes.
Assign all the desired roles from the "Available Roles" section to the "Assigned Roles" section. This assigns the selected client roles to the user.

## Get Configuration From OpenID Configuration Endpoint

Keycloak is OpenID Connect and OAuth2 compliant. Therefore, an openID configuration endpoint URL is exposed. In order to obtain the openID configuration,
make a GET request to the following URL:

```GET http(s)://<keycloak-host>:<keycloak-port>/auth/realms/<realm>/.well-known/openid-configuration```
