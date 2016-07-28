# AnimatedX
The Bling City back end.

#### Software stack:
- Java 7
- WildFly 8
- Spring Framework 4
- MariaDB 10

## Setup

#### Required software for development:

- JDK 7
- Maven 3.1.0+
- WildFly 8
- MariaDB 10 (Currently 10.0.12)

Maven handles all dependencies.

### Setting up MariaDB:

See [animatedx-db/README.md](animatedx-db/README.md)

### Setting up WildFly domain:

See [wildfly/README.md](wildfly/README.md)

## Deploying

WildFly must be running to deploy the application:

    $WILDFLY_ROOT/bin/standalone.sh --server-config=standalone-full-ha.xml

To deploy AnimatedX the project has to be build first, using the following command, in the root directory:

    mvn clean install

The, cd into the animatedx-ear directory, i.e. `$PROJECT_ROOT/animatedx-ear`, and deploy using the WildFly plugin:

    mvn wildfly:deploy

This redeploys the application if it already deployed.

## Developer Guidelines

See [https://animatedgames.atlassian.net/wiki/display/DEV/IntelliJ](https://animatedgames.atlassian.net/wiki/display/DEV/IntelliJ)

## Git Flow/Branching model

See [https://animatedgames.atlassian.net/wiki/display/DEV/Branching](https://animatedgames.atlassian.net/wiki/display/DEV/Branching)
