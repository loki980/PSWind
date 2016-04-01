package com.lokico.PSWind;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jeff on 3/31/2016.
 */
public class NOAAUnitTests {
    @Test
    public void isNoaaResponseContentConverted() {
        String rawResponse = RAW_RESPONSE_1;
        String expectedConvertedResponse = EXPECTED_CONVERTED_RESPONSE_1;
        String convertedResponse = NOAA.modifyRawResponse(rawResponse);
        boolean isConversionSuccess =
                convertedResponse.equals(expectedConvertedResponse);
        assertTrue(isConversionSuccess);
    }

    // An example raw response from http://www.nwwind.net/regionfcst.php?fcst=70
    static final String RAW_RESPONSE_1 =
            "\n" +
                    "<html> \n" +
                    "<head> \n" +
                    "<title>nwwind.net - Marine Wx Forecasts</title> \n" +
                    "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">\n" +
                    "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\">\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">\n" +
                    "</head> \n" +
                    "<body>\n" +
                    "<table> \n" +
                    "<tr>\n" +
                    "  <td class=alignleft_bottom_border colspan=3>\n" +
                    "    <b><a href=\"http://nwwind.net\">NWwind.net</a></b> - Marine Wx Forecasts -\n" +
                    "<a class=blue href=./wind.php?region=6>Wind Reports</a> -      <a href=\"http://nwwind.net/v1\">Old Site</a>\n" +
                    "  </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp&nbspWA: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"coastal waters forecast for washington\" href=\"/regionfcst.php?fcst=20\">coast</a>\n" +
                    " | <a title=\"west entrance u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=30\">western straits</a>\n" +
                    " | <a title=\"central u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=40\">central straits</a>\n" +
                    " | <a title=\"eastern u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=50\">eastern straits</a>\n" +
                    " | <a title=\"northern inland waters\" href=\"/regionfcst.php?fcst=10\">n.sound</a>\n" +
                    " | <a title=\"admiralty inlet\" href=\"/regionfcst.php?fcst=60\">admiralty</a>\n" +
                    " | <strong>sound</strong>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp&nbspOR: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"cape shoalwater to cascade head\" href=\"/regionfcst.php?fcst=200\">north coast</a>\n" +
                    " | <a title=\"florence to cape blanco\" href=\"/regionfcst.php?fcst=210\">central coast</a>\n" +
                    " | <a title=\"cape blanco to cape st. george\" href=\"/regionfcst.php?fcst=220\">south coast</a>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp&nbspBC: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"georgia strait\" href=\"/regionfcst.php?fcst=300\">georgia straits</a>\n" +
                    " | <a title=\"howe sound\" href=\"/regionfcst.php?fcst=310\">howe sound</a>\n" +
                    " | <a title=\"haro strait\" href=\"/regionfcst.php?fcst=320\">haro strait</a>\n" +
                    " | <a title=\"juan de fuca strait\" href=\"/regionfcst.php?fcst=330\">juan de fuca st</a>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp&nbspState: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"wa\" href=\"/dayfcst.php?state=wa\">wa</a>\n" +
                    " | <a title=\"or\" href=\"/dayfcst.php?state=or\">or</a>\n" +
                    " | <a title=\"bc\" href=\"/dayfcst.php?state=bc\">bc</a>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td>&nbsp</td>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft_bottom_border colspan=3>Latest marine forecasts for \n" +
                    "\t\t<A  title='Source forecast' href=http://weather.noaa.gov/pub/data/forecasts/marine/coastal/pz/pzz135.txt>puget sound</A>\n" +
                    "\n" +
                    "\n" +
                    "\t \n" +
                    "\t\t<A title='Alternate forecast' href=http://www.atmos.washington.edu/data/marine_report.html>(alternate)</A>\n" +
                    "</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft_bottom_border colspan=3>.synopsis for the northern and central washington coastal and inland waters...high pres off the washington coast with lower pres e of the cascades will result in onshore or westerly flow of varying strength through saturday. the flow will weaken saturday night due to the high shifting inland. weak onshore flow will prevail on sunday. a cold front will sweep across the area early mon for strong onshore flow. \n" +
                    "</td>\n" +
                    "</tr>\n" +
                    "\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>3/31</td>\n" +
                    "\t<td class=alignleft><b>Thu night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less becoming variable </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/1</td>\n" +
                    "\t<td class=alignleft><b>Fri</b></td>\n" +
                    "\t<td class=alignleft>sw wind 10 kt or less </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/1</td>\n" +
                    "\t<td class=alignleft><b>Fri night</b></td>\n" +
                    "\t<td class=alignleft>nw wind 5 to 15 kt becoming s </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/2</td>\n" +
                    "\t<td class=alignleft><b>Sat</b></td>\n" +
                    "\t<td class=alignleft>s wind 10 kt or less becoming n </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/2</td>\n" +
                    "\t<td class=alignleft><b>Sat night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/3</td>\n" +
                    "\t<td class=alignleft><b>Sun</b></td>\n" +
                    "\t<td class=alignleft>light wind </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/3</td>\n" +
                    "\t<td class=alignleft><b>Sun night</b></td>\n" +
                    "\t<td class=alignleft>s wind 5 to 15 kt </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/4</td>\n" +
                    "\t<td class=alignleft><b>Mon</b></td>\n" +
                    "\t<td class=alignleft>sw wind 10 to 20 kt except nw 10 kt or less over the n\n" +
                    " sound </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/5</td>\n" +
                    "\t<td class=alignleft><b>Tue</b></td>\n" +
                    "\t<td class=alignleft>s wind 15 to 25 kt </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/7</td>\n" +
                    "\t<td class=alignleft><b>Thu night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less becoming variable </td>\n" +
                    "</tr> \n" +
                    "</table>\n" +
                    "<script type=\"text/javascript\">\n" +
                    "var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\n" +
                    "document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\n" +
                    "</script>\n" +
                    "<script type=\"text/javascript\">\n" +
                    "try {\n" +
                    "var pageTracker = _gat._getTracker(\"UA-1502737-3\");\n" +
                    "pageTracker._trackPageview();\n" +
                    "} catch(err) {}</script>\n" +
                    "</body> \n" +
                    "</html> ";

    // Expected conversion for RAW_RESPONSE_1
    static final String EXPECTED_CONVERTED_RESPONSE_1 =
            "\n" +
                    "<html> \n" +
                    "<head> \n" +
                    "<title>nwwind.net - Marine Wx Forecasts</title> \n" +
                    "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">\n" +
                    "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\">\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">\n" +
                    "</head> \n" +
                    "<body>\n" +
                    "<table> \n" +
                    "<tr>\n" +
                    "  \n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp;&nbsp;WA: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"coastal waters forecast for washington\" href=\"/regionfcst.php?fcst=20\">coast</a>\n" +
                    " | <a title=\"west entrance u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=30\">western straits</a>\n" +
                    " | <a title=\"central u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=40\">central straits</a>\n" +
                    " | <a title=\"eastern u.s. waters strait of juan de fuca\" href=\"/regionfcst.php?fcst=50\">eastern straits</a>\n" +
                    " | <a title=\"northern inland waters\" href=\"/regionfcst.php?fcst=10\">n.sound</a>\n" +
                    " | <a title=\"admiralty inlet\" href=\"/regionfcst.php?fcst=60\">admiralty</a>\n" +
                    " | <strong>sound</strong>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp;&nbsp;OR: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"cape shoalwater to cascade head\" href=\"/regionfcst.php?fcst=200\">north coast</a>\n" +
                    " | <a title=\"florence to cape blanco\" href=\"/regionfcst.php?fcst=210\">central coast</a>\n" +
                    " | <a title=\"cape blanco to cape st. george\" href=\"/regionfcst.php?fcst=220\">south coast</a>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>&nbsp;&nbsp;State: \n" +
                    "\t</td>\n" +
                    "\t<td colspan=2 class=alignleft>\n" +
                    "\t<a title=\"wa\" href=\"/dayfcst.php?state=wa\">wa</a>\n" +
                    " | <a title=\"or\" href=\"/dayfcst.php?state=or\">or</a>\n" +
                    " | <a title=\"bc\" href=\"/dayfcst.php?state=bc\">bc</a>\n" +
                    " | \n" +
                    "\t</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td>&nbsp;</td>\n" +
                    "<tr>\n" +
                    "\t\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t\n" +
                    "</tr>\n" +
                    "\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>3/31</td>\n" +
                    "\t<td class=alignleft><b>Thu night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less becoming variable </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/1</td>\n" +
                    "\t<td class=alignleft><b>Fri</b></td>\n" +
                    "\t<td class=alignleft>sw wind 10 kt or less </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/1</td>\n" +
                    "\t<td class=alignleft><b>Fri night</b></td>\n" +
                    "\t<td class=alignleft>nw wind 5 to 15 kt becoming s </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/2</td>\n" +
                    "\t<td class=alignleft><b>Sat</b></td>\n" +
                    "\t<td class=alignleft>s wind 10 kt or less becoming n </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/2</td>\n" +
                    "\t<td class=alignleft><b>Sat night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/3</td>\n" +
                    "\t<td class=alignleft><b>Sun</b></td>\n" +
                    "\t<td class=alignleft>light wind </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/3</td>\n" +
                    "\t<td class=alignleft><b>Sun night</b></td>\n" +
                    "\t<td class=alignleft>s wind 5 to 15 kt </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/4</td>\n" +
                    "\t<td class=alignleft><b>Mon</b></td>\n" +
                    "\t<td class=alignleft>sw wind 10 to 20 kt except nw 10 kt or less over the n\n" +
                    " sound </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/5</td>\n" +
                    "\t<td class=alignleft><b>Tue</b></td>\n" +
                    "\t<td class=alignleft>s wind 15 to 25 kt </td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "\t<td class=alignleft>4/7</td>\n" +
                    "\t<td class=alignleft><b>Thu night</b></td>\n" +
                    "\t<td class=alignleft>n wind 10 kt or less becoming variable </td>\n" +
                    "</tr> \n" +
                    "</table>\n" +
                    "<script type=\"text/javascript\">\n" +
                    "var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\n" +
                    "document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\n" +
                    "</script>\n" +
                    "<script type=\"text/javascript\">\n" +
                    "try {\n" +
                    "var pageTracker = _gat._getTracker(\"UA-1502737-3\");\n" +
                    "pageTracker._trackPageview();\n" +
                    "} catch(err) {}</script>\n" +
                    "</body> \n" +
                    "</html> ";

}
