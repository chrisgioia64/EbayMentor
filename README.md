
**Example**
```
mvn test -Dlocale=it -Dbrowser=firefox
```
This uses a firefox browser with a locale of `it` (Italy).

**Configuration**

There are two environment variables `browser` and `locale`.
In order to set their values, they first attempt to first read from a system property. If that doesn't exist, they use the general environment file (`dev.properties` unless otherwise specified). If that doesn't exist, they use a default value.

- `browser`: the browser type to use for tests. The values come from the `name` field of the enum `BrowserType`. (possible values: `chrome`, `firefox`, `edge`).
- `locale`: the ebay website locale to use, which specifies info such as the base url and the expected title of the home page. See the list of locales in `src/test/resources/locale` (possible values: `us`, `uk`, `it`)