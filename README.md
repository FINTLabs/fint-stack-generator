# fint-stack-generator

This tool generates a FINT API stack Docker Compose file based on a small
configuration file.

The configuration file looks like this:

```yaml
environment: beta
stack: utdanning-elev
uri: /utdanning/elev
repository: dtr.rogfk.no/fint-beta
version: 0.6.2-3.0.0
port: 8250
```

The tool is a web application serving the controller at `/stacks`
