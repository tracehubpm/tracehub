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
@todo #113:45min Migrate estimate.xsl to vsheets.
 We should migrate estimate.xsl sheet into vsheets repo.
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
      <xsl:if test="number(//cost) &lt; number(//min-estimate) or number(//cost) &gt; number(//max-estimate)">
        <error>Specified estimate (<xsl:value-of select="//cost"/>) is not in allowed range: <xsl:value-of select="//min-estimate"/> (`backlog:rules:min-estimate`) and <xsl:value-of select="//max-estimate"/> (`backlog:rules:max-estimate)`.</error>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
