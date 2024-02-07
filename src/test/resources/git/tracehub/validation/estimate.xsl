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
        <error>Specified estimate (<xsl:value-of select="//cost"/>) is not in allowed range: <xsl:value-of select="//min-estimate"/> (`issues:backlog:rules:min-estimate`) and <xsl:value-of select="//max-estimate"/> (`issues:backlog:rules:max-estimate)`.</error>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
