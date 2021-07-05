# Baubotanik

**This is experimnetal**

Turn EDN data structures into GraphViz DOT files with a pinch of
inheriting styles and a global styling section.

Since GraphViz only allows for mass-styling things with "from here on
things are styled like this", it's hard to keep a structure in the DOT
file, that keeps logical things together.

There are [sample files and the according results](./dev-resources) (as
used in the tests).

## Usage

Build an *uberjar*:

```
lein uberjar
```

Run the resulting uberjar with source and target file (or leave the
later out to have the output on stdout):

```sh
java -jar target/uberjar/baubotanik-0.1.0-SNAPSHOT-standalone.jar dev-resources/sample.edn | dot -Tpng | display -
```

- or -

Build a *native image* (requires `native-image` from Graal to be
installed):

```
lein native-image
```

Run:

```sh
./target/default+uberjar/baubotanik dev-resources/sample.edn | dot -Tpng | display -
```

## License

Copyright © 2020 Christoph Frick. All Rights Reserved.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
