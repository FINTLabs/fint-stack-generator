package no.fint.stack.generator.http

/*
* Parsed according to http://www.ietf.org/rfc/rfc2617.txt
* From https://github.com/spinnaker/clouddriver/blob/d24e722f386ac5ee8bdd9dbc20c847ea0a33ebbd/clouddriver-docker/src/main/groovy/com/netflix/spinnaker/clouddriver/docker/registry/api/v2/auth/DockerBearerTokenService.groovy#L106
*/

class WwwAuthenticateHeaderParser {

    static def parse(String header) {

        def result = [:]

        def space = header.indexOf(' ')
        if (space == -1) {
            throw new IllegalArgumentException("Www-Authenticate header Illegal format: '$header'.")
        }
        result.scheme = header.substring(0, space)
        result.attributes = [:]
        header = header.substring(space + 1)

        // Each parameter has the form <token>=(<token>|<quoted-string>).
        while (header.length() > 0) {
            String key
            String value

            def keyEnd = header.indexOf("=")
            if (keyEnd == -1) {
                throw new IllegalArgumentException("Www-Authenticate header terminated with junk: '$header'.")
            }

            key = header.substring(0, keyEnd)
            header = header.substring(keyEnd + 1)
            if (header.length() == 0) {
                throw new IllegalArgumentException("Www-Authenticate header unmatched parameter key: '$key'.")
            }

            // Parse a quoted string.
            if (header[0] == '"') {
                header = header.substring(1)

                def valueEnd = header.indexOf('"')
                if (valueEnd == -1) {
                    throw new IllegalArgumentException('Www-Authenticate header has unterminated " (quotation mark).')
                }

                value = header.substring(0, valueEnd)
                header = header.substring(valueEnd + 1)

                if (header.length() != 0) {
                    if (header[0] != ",") {
                        throw new IllegalArgumentException("Www-Authenticate header params must be separated by , (comma).")
                    }
                    header = header.substring(1)
                }
            } else { // Parse an unquoted token.
                def valueEnd = header.indexOf(",")

                // In the case of the last parameter, there will be no terminating ',' character.
                if (valueEnd == -1) {
                    value = header
                    header = ""
                } else {
                    value = header.substring(0, valueEnd)
                    header = header.substring(valueEnd + 1)
                }
            }

            result.attributes."$key" = value
        }

        return result

    }
}
