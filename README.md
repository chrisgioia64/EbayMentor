
**Example configuration**
```
mvn test -Dlocale=it -Dbrowser=firefox
```

Using a firefox browser with a locale of `it` (Italy).

**Configuration**

There are two environment variables which first read from the system property, then the environment file (`dev.properties` unless otherwise specified):
- `browser`: the browser type to use for tests. (default: `chrome`)
- `locale`: the ebay website locale to use, which specifies info such as the base url and the expected title of the home page. (default: `us`) 