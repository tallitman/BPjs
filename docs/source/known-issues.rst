.. _known_issues:

===============
Known Issues
===============

* BPjs does not support Javascript NativeArrays. This is due to internal Rhino issues (issue_449_, issue_437_).
* To allow verification, all variables must be declared (e.g. using ``var``). Non-declared variables are not supported by the current serialization/de-serialization system.




.. _issue_449: https://github.com/mozilla/rhino/issues/449
.. _issue_437: https://github.com/mozilla/rhino/issues/437