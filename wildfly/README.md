# WildFly Domain

On local development environments we run WildFly in standalone mode, while on test servers and on the staging and production environments we run in domain mode.

## Installation

Download the "Application Server Distribution" package of WildFly 8.0 from [http://wildfly.org/](http://wildfly.org/), and unzip it.

## <a name="upgrade"><a/> Upgrades

Some changes to the overall configuration of the project, or changes to WildFly require the WildFly configurations to be removed and re-created.

__*To do this, fire up WildFly from the command shell as explained above, connect to the administration console, and remove the data source(s), driver(s)
deployments and jms queues, in that order.*__

The re-run the setup from the [JDBC Driver Installation](#jdbd-driver-installation)
If a configuration change occurs, or the jdbc driver is updated, the easiest way is to manually remove all setup configurations below, and then re-run the setup. This
does not include adding the user.

## Setup

##### Directory and file types

    The naming standard in this document is Linux, so if you're running on Windows, `/` should be changed to `\` and `.sh` to `.bat`.

First, a management user needs to be created, run the following command in the WildFly root directory:

    $WILDFLY_ROOT/bin/add-user.sh ccsl admin

### <a name="jdbc-driver-installation"></a> JDBC Driver Installation

Next, the JDBC driver needs to be deployed and the data source configured. This is done using maven and the WildFly plugin. For this to work WildFly
needs to be running, start it in standalone mode:

    $WILDFLY_ROOT/bin/standalone.sh --server-config=standalone-full-ha.xml

> If you're running on Windows and you get an error about WildFly being unable to bind to an address, one reason can be the NVIDIA Network Service using port 9990.
If so you need to disable the service.

The following commands should be run in the `$PROJECT_ROOT/wildfly/domain` directory of this repository:

    mvn clean install -Pwildfly-setup

##### Default data source

In Java EE7, the specification says that a default data source has to exist on the server. In WildFly this is the `ExampleDS` data source bound to
`java:jboss/datasources/ExampleDS`. If it's not present WildFly won't start. So do not delete it.

### Memory Configuration

By default, WildFly comes configured with very low memory settings. To be able to run our applications the memory settings have to be increased.

#### Linux

The configuration file is `$WILDFLY_ROOT/bin/standalone.conf`

At around line 50 there is a section similar to this:

    if [ "x$JAVA_OPTS" = "x" ]; then
       JAVA_OPTS="-Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"
       JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true"
    else
       echo "JAVA_OPTS already set in environment; overriding default settings with values: $JAVA_OPTS"
    fi


replace it with this:

    if [ "x$JAVA_OPTS" = "x" ]; then
    #   JAVA_OPTS="-Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"
       JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1512m -XX:MaxPermSize=512m -Djava.net.preferIPv4Stack=true"
       JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true"
    else
       JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1512m -XX:MaxPermSize=512m -Djava.net.preferIPv4Stack=true"
       echo "JAVA_OPTS already set in environment; overriding default settings with values: $JAVA_OPTS"
    fi

#### Windows

The configuration file is `$WILDFLY_ROOT\bin\standalone.conf.bat`

At around line 18 there's a section similar to this:

    if not "x%JAVA_OPTS%" == "x" (
      echo "JAVA_OPTS already set in environment; overriding default settings with values: %JAVA_OPTS%"
      goto JAVA_OPTS_SET
    )

replace it with this:

    if not "x%JAVA_OPTS%" == "x" (
      set "JAVA_OPTS=%JAVA_OPTS% -Xms512M -Xmx1512M -XX:MaxPermSize=512M"
      echo "JAVA_OPTS already set in environment; overriding default settings with values: %JAVA_OPTS%"
      goto JAVA_OPTS_SET
    )

At around line 49 there is a line similar to this:

    set "JAVA_OPTS=-Xms64M -Xmx512M -XX:MaxPermSize=256M"

comment it out, and add this line after:

    set "JAVA_OPTS=-Xms512M -Xmx1512M -XX:MaxPermSize=512M"

## IntelliJ

### Application Servers

An Application Server entry has to be created in IntelliJ, under `Settings -> Application Servers`. Create a new and point it to the WildFly installation directory.
Name it `WildFly 8.0.0.Final`.

### Artifacts

WildFly requires exploded artifacts to end with `.war`, and since Maven doesn't create the exploded artifact like that, and IntelliJ uses Maven's artifacts,
a "custom" artifact has to be created.

In `Project Structure -> Artifacts`, create a new `exploded` artifact of the same type for each existing artifact, from that module, and rename it with the postfix
"on WildFly", e.g `Web Application: Exploded -> From Module...` and chose the casino module and rename it `casino:war exploded on WildFly`.

When creating artifacts in IntelliJ, IntelliJ doesn't automatically include all dependent modules, and this is shown with an error ribbon and button in the lower right
corner of the window. Click `Fix...` and choose `Add all missing dependencies of '<module> to the artifact'`. If any dependencies are added later to the module this
won't get picked up by IntelliJ, and will have to be added manually. This will become evident when deploying and the server throws a `ClassNotFoundException`.

### Run Configurations

IntelliJ requires a pointer to the application server that an artifact is to be deployed to. Use the Application Server entry create above.

The Web Profile is the default profile in WildFly, we require the full profile, so all the Run Configurations must be updated with a parameter.
In each Run Configuration go to the `Startup/Connection` tab, and for both the `Run` and `Debug` command, un-check the `Use default` check box and add the
`--server-config=standalone-full-ha.xml` text to the `parameters` window.

## Administration

The admin console can be accessed through the url [http://localhost:9990/console](http://localhost:9990/console).
