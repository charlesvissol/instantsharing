Documentation

# Libraries

- [DropzoneJS](https://www.dropzonejs.com/)





# Tomcat Configuration

In the `web.xml` configuration file of Tomcat, set `session-timeout` to `-1` for infinite session time.

Sample:

```xml
    <session-config>
        <session-timeout>-1</session-timeout>
    </session-config>
```

instantsharing assumes the session doesn't end.