<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2023-2024 Tracehub.git

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<!--
@todo #113:45min Migrate words.xsl to vsheets.
 We should migrate words.xsl sheet into vsheets repo.
 Also, we should create a spec, like
 <a href="https://github.com/tracehubpm/vsheets/blob/master/spec/struct_spec.rb">this</a>.
 Don't forget to remove this puzzle.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="yes" indent="no"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="data/errors">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <!--
      @todo #113:25min Count only words, not a symbols, and exclude spaces.
       We should count only words, not a symbol length, like now.
       Also, we should not count spaces as words. Don't forget to remove this
       puzzle before migrating sheet to vsheets repo.
      -->
      <xsl:if test="//description/string-length() &lt; //min-words">
        <error>Specified task description (<xsl:value-of select="//description/string-length()"/>) is too small, minimal amount of words is <xsl:value-of select="//min-words"/> (`backlog:rules:min-words`).</error>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
