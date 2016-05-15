package com.lokico.PSWind;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class WindSensorParser {

    static final private String TAG = "WindSensorParser";

    static final String WINDSENSOR_TEST_DATA = "<markers>\n" +
            "<marker id=\"WOTW07\" label=\"Hat Island\" lat=\"48.0194\" lng=\"-122.334\" wind=\"7\" gust=\"9\" angle=\"157\" timestamp=\"1463160133\"/>\n" +
            "<marker id=\"KKLS\" label=\"Kelso\" lat=\"46.1167\" lng=\"-122.9\" wind=\"5\" gust=\"0\" angle=\"157\" timestamp=\"1463157300\"/>\n" +
            "<marker id=\"CYVR\" label=\"Vancouver International\" lat=\"49.1833\" lng=\"-123.167\" wind=\"10\" gust=\"0\" angle=\"112\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"CWVF\" label=\"Sand Heads CS\" lat=\"49.1\" lng=\"-123.3\" wind=\"9\" gust=\"0\" angle=\"135\" timestamp=\"1463157900\"/>\n" +
            "<marker id=\"46206\" label=\"Buoy 46206\" lat=\"48.8\" lng=\"-126\" wind=\"4\" gust=\"7\" angle=\"112\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"C6980\" label=\"Birch Bay\" lat=\"48.9152\" lng=\"-122.74\" wind=\"0\" gust=\"0\" angle=\"90\" timestamp=\"1216491000\"/>\n" +
            "<marker id=\"IBCDELTA4\" label=\"Delta\" lat=\"49.02\" lng=\"-123.08\" wind=\"0\" gust=\"5\" angle=\"22\" timestamp=\"1458761283\"/>\n" +
            "<marker id=\"WA017\" label=\"Point Roberts\" lat=\"48.9783\" lng=\"-123.067\" wind=\"7\" gust=\"11\" angle=\"337\" timestamp=\"1334968500\"/>\n" +
            "<marker id=\"TBLAI\" label=\"Blaine\" lat=\"49\" lng=\"-122.735\" wind=\"3\" gust=\"9\" angle=\"270\" timestamp=\"1463014800\"/>\n" +
            "<marker id=\"KWABLAIN7\" label=\"Birch Bay\" lat=\"48.9\" lng=\"-122.77\" wind=\"4\" gust=\"8\" angle=\"0\" timestamp=\"1375220842\"/>\n" +
            "<marker id=\"CHYW1\" label=\"Cherry Point\" lat=\"48.863\" lng=\"-122.758\" wind=\"11\" gust=\"14\" angle=\"135\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"C1394\" label=\"Sandy Pt. Shores\" lat=\"48.8009\" lng=\"-122.707\" wind=\"10\" gust=\"21\" angle=\"247\" timestamp=\"1463157840\"/>\n" +
            "<marker id=\"WA010\" label=\"Locust Beach\" lat=\"48.7767\" lng=\"-122.562\" wind=\"3\" gust=\"5\" angle=\"337\" timestamp=\"1463160000\"/>\n" +
            "<marker id=\"WA015\" label=\"Bellingham BCS\" lat=\"48.7606\" lng=\"-122.508\" wind=\"4\" gust=\"0\" angle=\"292\" timestamp=\"1388683140\"/>\n" +
            "<marker id=\"CWEZ\" label=\"Saturna Island\" lat=\"48.7833\" lng=\"-123.05\" wind=\"3\" gust=\"0\" angle=\"90\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"CWZO\" label=\"Kelp Reefs: B. C.\" lat=\"48.55\" lng=\"-123.233\" wind=\"9\" gust=\"0\" angle=\"202\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"46087\" label=\"Buoy 46087\" lat=\"48.5\" lng=\"-124.7\" wind=\"13\" gust=\"16\" angle=\"270\" timestamp=\"1463156400\"/>\n" +
            "<marker id=\"KBVS\" label=\"Skagit Airport\" lat=\"48.4708\" lng=\"-122.421\" wind=\"5\" gust=\"0\" angle=\"112\" timestamp=\"1463157300\"/>\n" +
            "<marker id=\"PBFW1\" label=\"Padilla Bay\" lat=\"48.4639\" lng=\"-122.468\" wind=\"6\" gust=\"9\" angle=\"135\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"KWALACON1\" label=\"Shelter Bay\" lat=\"48.38\" lng=\"-122.52\" wind=\"3\" gust=\"6\" angle=\"247\" timestamp=\"1251767651\"/>\n" +
            "<marker id=\"TTIW1\" label=\"Tatoosh Island\" lat=\"48.39\" lng=\"-124.74\" wind=\"20\" gust=\"0\" angle=\"225\" timestamp=\"1432512000\"/>\n" +
            "<marker id=\"KNUW\" label=\"Whidbey Island\" lat=\"48.3492\" lng=\"-122.651\" wind=\"5\" gust=\"0\" angle=\"112\" timestamp=\"1463154960\"/>\n" +
            "<marker id=\"SISW1\" label=\"Smith Island\" lat=\"48.32\" lng=\"-122.84\" wind=\"5\" gust=\"6\" angle=\"90\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"46088\" label=\"Buoy 46088\" lat=\"48.3\" lng=\"-123.2\" wind=\"2\" gust=\"4\" angle=\"67\" timestamp=\"1463156400\"/>\n" +
            "<marker id=\"WA007\" label=\"City Beach\" lat=\"48.29\" lng=\"-122.65\" wind=\"5\" gust=\"0\" angle=\"292\" timestamp=\"1388698252\"/>\n" +
            "<marker id=\"STANW\" label=\"Stanwood HS\" lat=\"48.2428\" lng=\"-122.333\" wind=\"3\" gust=\"3\" angle=\"292\" timestamp=\"1323875940\"/>\n" +
            "<marker id=\"WA012\" label=\"Point Wilson\" lat=\"48.1439\" lng=\"-122.755\" wind=\"9\" gust=\"0\" angle=\"0\" timestamp=\"1459895122\"/>\n" +
            "<marker id=\"C5931\" label=\"Sekiu\" lat=\"48.2663\" lng=\"-124.3\" wind=\"0\" gust=\"0\" angle=\"180\" timestamp=\"1317349260\"/>\n" +
            "<marker id=\"TKEYS\" label=\"Keystone\" lat=\"48.1592\" lng=\"-122.672\" wind=\"6\" gust=\"0\" angle=\"67\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"WA008\" label=\"Port Angeles\" lat=\"48.1333\" lng=\"-123.4\" wind=\"5\" gust=\"0\" angle=\"0\" timestamp=\"1459895122\"/>\n" +
            "<marker id=\"WA001\" label=\"Jetty Island\" lat=\"48.0035\" lng=\"-122.228\" wind=\"6\" gust=\"3\" angle=\"90\" timestamp=\"1409602200\"/>\n" +
            "<marker id=\"C1824\" label=\"Langley\" lat=\"47.9967\" lng=\"-122.48\" wind=\"2\" gust=\"4\" angle=\"112\" timestamp=\"1277393880\"/>\n" +
            "<marker id=\"KWAFREEL8\" label=\"Useless Bay\" lat=\"47.99\" lng=\"-122.51\" wind=\"3\" gust=\"7\" angle=\"180\" timestamp=\"1441839360\"/>\n" +
            "<marker id=\"KWAFREEL6\" label=\"Mutiny Bay\" lat=\"48.01\" lng=\"-122.56\" wind=\"15\" gust=\"15\" angle=\"315\" timestamp=\"1436731836\"/>\n" +
            "<marker id=\"KUIL\" label=\"Quillayute Airport\" lat=\"47.9375\" lng=\"-124.555\" wind=\"6\" gust=\"0\" angle=\"90\" timestamp=\"1463107980\"/>\n" +
            "<marker id=\"KPAE\" label=\"Everett: Snohomish County Airport\" lat=\"47.9231\" lng=\"-122.283\" wind=\"3\" gust=\"0\" angle=\"157\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"WA002\" label=\"Point No Point\" lat=\"47.9167\" lng=\"-122.533\" wind=\"3\" gust=\"0\" angle=\"0\" timestamp=\"1459895122\"/>\n" +
            "<marker id=\"KWAPORTL9\" label=\"Shine\" lat=\"47.87\" lng=\"-122.67\" wind=\"0\" gust=\"0\" angle=\"157\" timestamp=\"1276616011\"/>\n" +
            "<marker id=\"WA018\" label=\"Hood Canal\" lat=\"47.8606\" lng=\"-122.626\" wind=\"9\" gust=\"0\" angle=\"0\" timestamp=\"1333054800\"/>\n" +
            "<marker id=\"KWAPOULS5\" label=\"Lofall\" lat=\"47.81\" lng=\"-122.65\" wind=\"12\" gust=\"20\" angle=\"22\" timestamp=\"1458761435\"/>\n" +
            "<marker id=\"AS275\" label=\"Edmonds\" lat=\"47.8261\" lng=\"-122.338\" wind=\"2\" gust=\"5\" angle=\"202\" timestamp=\"1308867360\"/>\n" +
            "<marker id=\"001D0A000A36\" label=\"Edmonds\" lat=\"47.8\" lng=\"-122.4\" wind=\"10\" gust=\"4\" angle=\"180\" timestamp=\"1435029360\"/>\n" +
            "<marker id=\"KWASHORE5\" label=\"Central Market Shoreline\" lat=\"47.74\" lng=\"-122.35\" wind=\"7\" gust=\"11\" angle=\"292\" timestamp=\"1458761424\"/>\n" +
            "<marker id=\"TKING\" label=\"Kingston\" lat=\"47.794\" lng=\"-122.494\" wind=\"3\" gust=\"0\" angle=\"202\" timestamp=\"1463157600\"/>\n" +
            "<marker id=\"DRYW1\" label=\"DRY CREEK\" lat=\"47.7272\" lng=\"-120.54\" wind=\"2\" gust=\"13\" angle=\"22\" timestamp=\"1463157420\"/>\n" +
            "<marker id=\"WA006\" label=\"Juanita\" lat=\"47.7\" lng=\"-122.24\" wind=\"2\" gust=\"0\" angle=\"0\" timestamp=\"1458761097\"/>\n" +
            "<marker id=\"46041\" label=\"Buoy 46041\" lat=\"47.3\" lng=\"-124.7\" wind=\"11\" gust=\"13\" angle=\"180\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"DESW1\" label=\"Destruction Island\" lat=\"47.68\" lng=\"-124.49\" wind=\"3\" gust=\"3\" angle=\"157\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"WPOW1\" label=\"West Point\" lat=\"47.66\" lng=\"-122.44\" wind=\"11\" gust=\"13\" angle=\"202\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"EVPTB\" label=\"520 Bridge\" lat=\"47.64\" lng=\"-122.259\" wind=\"5\" gust=\"8\" angle=\"315\" timestamp=\"1442442000\"/>\n" +
            "<marker id=\"WA019\" label=\"I90 Bridge\" lat=\"47.5899\" lng=\"-122.27\" wind=\"1\" gust=\"10\" angle=\"292\" timestamp=\"1442438400\"/>\n" +
            "<marker id=\"WA013\" label=\"Lake Washington\" lat=\"47.6822\" lng=\"-122.247\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1419449122\"/>\n" +
            "<marker id=\"EBSW1\" label=\"Seattle\" lat=\"47.605\" lng=\"-122.338\" wind=\"4\" gust=\"6\" angle=\"135\" timestamp=\"1463156640\"/>\n" +
            "<marker id=\"KWASEATT109\" label=\"University District\" lat=\"47.65\" lng=\"-122.3\" wind=\"20\" gust=\"24\" angle=\"0\" timestamp=\"1458761405\"/>\n" +
            "<marker id=\"C2512\" label=\"Bremerton\" lat=\"47.565\" lng=\"-122.669\" wind=\"0\" gust=\"0\" angle=\"157\" timestamp=\"1232541300\"/>\n" +
            "<marker id=\"C2659\" label=\"Bellevue\" lat=\"47.5575\" lng=\"-122.177\" wind=\"2\" gust=\"6\" angle=\"180\" timestamp=\"1463158260\"/>\n" +
            "<marker id=\"TQUEE\" label=\"Queets\" lat=\"47.536\" lng=\"-124.333\" wind=\"1\" gust=\"3\" angle=\"90\" timestamp=\"1463111400\"/>\n" +
            "<marker id=\"WA009\" label=\"Alki Point\" lat=\"47.5762\" lng=\"-122.421\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1435168221\"/>\n" +
            "<marker id=\"KWASEATT112\" label=\"Beach Drive\" lat=\"47.55\" lng=\"-122.4\" wind=\"5\" gust=\"8\" angle=\"157\" timestamp=\"1375655256\"/>\n" +
            "<marker id=\"TFAUN\" label=\"Fauntleroy\" lat=\"47.527\" lng=\"-122.39\" wind=\"9\" gust=\"0\" angle=\"135\" timestamp=\"1463157900\"/>\n" +
            "<marker id=\"TSOUT\" label=\"Southworth\" lat=\"47.513\" lng=\"-122.495\" wind=\"6\" gust=\"0\" angle=\"112\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"KRNT\" label=\"Renton Airport\" lat=\"47.4944\" lng=\"-122.213\" wind=\"9\" gust=\"0\" angle=\"135\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KBFI\" label=\"Boeing Field\" lat=\"47.5458\" lng=\"-122.314\" wind=\"6\" gust=\"0\" angle=\"112\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KSEA\" label=\"Seatac\" lat=\"47.4447\" lng=\"-122.314\" wind=\"6\" gust=\"0\" angle=\"180\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KEAT\" label=\"Wenatchee Airport\" lat=\"47.3989\" lng=\"-120.207\" wind=\"3\" gust=\"0\" angle=\"0\" timestamp=\"1463151300\"/>\n" +
            "<marker id=\"WA014\" label=\"Point Robinson\" lat=\"47.3833\" lng=\"-122.367\" wind=\"8\" gust=\"0\" angle=\"0\" timestamp=\"1459895122\"/>\n" +
            "<marker id=\"KWAFOXIS2\" label=\"Fox Island, WA\" lat=\"47.22\" lng=\"-122.61\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1318444226\"/>\n" +
            "<marker id=\"KTIW\" label=\"Tacoma Narrows Airport\" lat=\"47.2675\" lng=\"-122.576\" wind=\"6\" gust=\"0\" angle=\"0\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KWATACOM16\" label=\"Tacoma/Dash Point\" lat=\"47.31\" lng=\"-122.43\" wind=\"4\" gust=\"11\" angle=\"22\" timestamp=\"1458761412\"/>\n" +
            "<marker id=\"TCMW1\" label=\"Tacoma\" lat=\"47.276\" lng=\"-122.418\" wind=\"6\" gust=\"7\" angle=\"90\" timestamp=\"1463156640\"/>\n" +
            "<marker id=\"KSHN\" label=\"Sanderson Field\" lat=\"47.2381\" lng=\"-123.141\" wind=\"3\" gust=\"0\" angle=\"0\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"C5218\" label=\"Olympia\" lat=\"46.9817\" lng=\"-122.853\" wind=\"1\" gust=\"4\" angle=\"112\" timestamp=\"1463155980\"/>\n" +
            "<marker id=\"KOLM\" label=\"Olympia Airport\" lat=\"46.9733\" lng=\"-122.903\" wind=\"5\" gust=\"0\" angle=\"225\" timestamp=\"1463154840\"/>\n" +
            "<marker id=\"KWAOCEAN8\" label=\"Damon Point\" lat=\"46.95\" lng=\"-124.14\" wind=\"17\" gust=\"23\" angle=\"315\" timestamp=\"1458761423\"/>\n" +
            "<marker id=\"WSTSP\" label=\"City of Westport\" lat=\"46.8922\" lng=\"-124.104\" wind=\"8\" gust=\"8\" angle=\"247\" timestamp=\"1323874380\"/>\n" +
            "<marker id=\"WPTW1\" label=\"Westport\" lat=\"46.927\" lng=\"-124.13\" wind=\"4\" gust=\"6\" angle=\"247\" timestamp=\"1463156640\"/>\n" +
            "<marker id=\"KWAGRAYL2\" label=\"Downtown\" lat=\"46.81\" lng=\"-124.09\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1301212260\"/>\n" +
            "<marker id=\"TOKW1\" label=\"Toke Point\" lat=\"46.708\" lng=\"-123.965\" wind=\"2\" gust=\"5\" angle=\"337\" timestamp=\"1463156640\"/>\n" +
            "<marker id=\"KHQM\" label=\"Bowerman Airport\" lat=\"46.9711\" lng=\"-123.92\" wind=\"5\" gust=\"0\" angle=\"292\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"C0799\" label=\"Ocean Park\" lat=\"46.4778\" lng=\"-124.05\" wind=\"1\" gust=\"2\" angle=\"112\" timestamp=\"1463158080\"/>\n" +
            "<marker id=\"C6208\" label=\"Long Beach\" lat=\"46.4434\" lng=\"-124.053\" wind=\"0\" gust=\"0\" angle=\"292\" timestamp=\"1283960160\"/>\n" +
            "<marker id=\"KAST\" label=\"Astoria\" lat=\"46.1569\" lng=\"-123.882\" wind=\"3\" gust=\"0\" angle=\"1\" timestamp=\"1463150220\"/>\n" +
            "<marker id=\"C5398\" label=\"Sequim\" lat=\"48.0425\" lng=\"-122.973\" wind=\"1\" gust=\"3\" angle=\"112\" timestamp=\"1463157300\"/>\n" +
            "<marker id=\"WA011\" label=\"Dungeness Lighthouse\" lat=\"48.1818\" lng=\"-123.11\" wind=\"4\" gust=\"0\" angle=\"67\" timestamp=\"1463159016\"/>\n" +
            "<marker id=\"KCLM\" label=\"Port Angeles Airport\" lat=\"48.1222\" lng=\"-123.505\" wind=\"3\" gust=\"0\" angle=\"45\" timestamp=\"1463136780\"/>\n" +
            "<marker id=\"KBLI\" label=\"Bellingham Airport\" lat=\"48.7994\" lng=\"-122.539\" wind=\"9\" gust=\"0\" angle=\"225\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"3CLO3\" label=\"Clatsop Spit\" lat=\"46.22\" lng=\"-124\" wind=\"3\" gust=\"0\" angle=\"67\" timestamp=\"1463154300\"/>\n" +
            "<marker id=\"AS782\" label=\"Tulalip\" lat=\"48.0408\" lng=\"-122.253\" wind=\"1\" gust=\"15\" angle=\"0\" timestamp=\"1453397880\"/>\n" +
            "<marker id=\"C6556\" label=\"Bellingham\" lat=\"48.7083\" lng=\"-122.508\" wind=\"1\" gust=\"3\" angle=\"225\" timestamp=\"1463126580\"/>\n" +
            "<marker id=\"D0619\" label=\"Bellingham\" lat=\"48.6635\" lng=\"-122.501\" wind=\"8\" gust=\"0\" angle=\"225\" timestamp=\"1390521840\"/>\n" +
            "<marker id=\"WA016\" label=\"Edison\" lat=\"48.5614\" lng=\"-122.438\" wind=\"23\" gust=\"0\" angle=\"292\" timestamp=\"1327419632\"/>\n" +
            "<marker id=\"KBVS\" label=\"Skagit Airport\" lat=\"48.4708\" lng=\"-122.421\" wind=\"5\" gust=\"0\" angle=\"112\" timestamp=\"1463157300\"/>\n" +
            "<marker id=\"KAWO\" label=\"Arlington Municipal\" lat=\"48.1667\" lng=\"-122.167\" wind=\"6\" gust=\"0\" angle=\"135\" timestamp=\"1463157300\"/>\n" +
            "<marker id=\"MRVJH\" label=\"Marysville\" lat=\"48.06\" lng=\"-122.17\" wind=\"2\" gust=\"0\" angle=\"180\" timestamp=\"1463047200\"/>\n" +
            "<marker id=\"FERRYFP\" label=\"Kingston-Edmonds Ferry\" lat=\"47.8023\" lng=\"-122.442\" wind=\"5\" gust=\"0\" angle=\"180\" timestamp=\"1463160000\"/>\n" +
            "<marker id=\"FERRYFT\" label=\"Seattle-Winslow Ferry\" lat=\"47.6054\" lng=\"-122.426\" wind=\"-1\" gust=\"0\" angle=\"1\" timestamp=\"1463160000\"/>\n" +
            "<marker id=\"FERRYFQ\" label=\"Fauntleroy-Southworth Ferry\" lat=\"47.5195\" lng=\"-122.447\" wind=\"7\" gust=\"0\" angle=\"22\" timestamp=\"1462851540\"/>\n" +
            "<marker id=\"FERRYFS\" label=\"Point Defiance-Tahlequah Ferry\" lat=\"47.3209\" lng=\"-122.512\" wind=\"6\" gust=\"0\" angle=\"135\" timestamp=\"1463160000\"/>\n" +
            "<marker id=\"KMWH\" label=\"Moses Lake Airport\" lat=\"47.1931\" lng=\"-119.313\" wind=\"22\" gust=\"28\" angle=\"225\" timestamp=\"1463154720\"/>\n" +
            "<marker id=\"lopw1\" label=\"Longview Buoy\" lat=\"46.108\" lng=\"-122.957\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1463154480\"/>\n" +
            "<marker id=\"CYVR\" label=\"Vancouver International\" lat=\"49.1833\" lng=\"-123.167\" wind=\"10\" gust=\"0\" angle=\"112\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"KWANMUKI2\" label=\"Harborview\" lat=\"47.96\" lng=\"-122.25\" wind=\"0\" gust=\"3\" angle=\"225\" timestamp=\"1419382325\"/>\n" +
            "<marker id=\"KWASEATT158\" label=\"Blue Ridge\" lat=\"47.7047\" lng=\"-122.377\" wind=\"3\" gust=\"15\" angle=\"112\" timestamp=\"1323672083\"/>\n" +
            "<marker id=\"KWAKALAM7\" label=\"Kalama\" lat=\"46.01\" lng=\"-122.85\" wind=\"3\" gust=\"8\" angle=\"315\" timestamp=\"1458761478\"/>\n" +
            "<marker id=\"C4910\" label=\"Kalama 2\" lat=\"46.0142\" lng=\"-122.841\" wind=\"1\" gust=\"10\" angle=\"180\" timestamp=\"1463157600\"/>\n" +
            "<marker id=\"C4577\" label=\"Kalama 3\" lat=\"45.9822\" lng=\"-122.793\" wind=\"1\" gust=\"2\" angle=\"67\" timestamp=\"1357010220\"/>\n" +
            "<marker id=\"KORCOLUM3\" label=\"Columbia City\" lat=\"45.88\" lng=\"-122.81\" wind=\"2\" gust=\"0\" angle=\"45\" timestamp=\"1259076808\"/>\n" +
            "<marker id=\"KORSTHEL4\" label=\"St. Helens\" lat=\"45.87\" lng=\"-122.8\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1388524629\"/>\n" +
            "<marker id=\"E5POR\" label=\"Sauvie Island\" lat=\"45.7681\" lng=\"-122.772\" wind=\"4\" gust=\"0\" angle=\"202\" timestamp=\"1463158800\"/>\n" +
            "<marker id=\"OR003\" label=\"Felida\" lat=\"45.7\" lng=\"-122.7\" wind=\"0\" gust=\"0\" angle=\"180\" timestamp=\"1443472680\"/>\n" +
            "<marker id=\"KSPB\" label=\"Scappoose\" lat=\"45.7692\" lng=\"-122.862\" wind=\"3\" gust=\"0\" angle=\"0\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"ODT10\" label=\"Fremont Bridge\" lat=\"45.5368\" lng=\"-122.684\" wind=\"5\" gust=\"0\" angle=\"45\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"KHIO\" label=\"Hillsboro Airport\" lat=\"45.5481\" lng=\"-122.954\" wind=\"7\" gust=\"0\" angle=\"135\" timestamp=\"1463122380\"/>\n" +
            "<marker id=\"WOTW00\" label=\"WOTW Headquarters\" lat=\"45.5529\" lng=\"-122.78\" wind=\"0\" gust=\"0\" angle=\"90\" timestamp=\"1316301188\"/>\n" +
            "<marker id=\"ODT06\" label=\"I-5 Bridge\" lat=\"45.6214\" lng=\"-122.673\" wind=\"6\" gust=\"0\" angle=\"90\" timestamp=\"1463155980\"/>\n" +
            "<marker id=\"ODT08\" label=\"I-205 Bridge\" lat=\"45.5914\" lng=\"-122.547\" wind=\"9\" gust=\"17\" angle=\"0\" timestamp=\"1411272600\"/>\n" +
            "<marker id=\"KORPORTL127\" label=\"Rivergate\" lat=\"45.6378\" lng=\"-122.766\" wind=\"9\" gust=\"0\" angle=\"157\" timestamp=\"1458761399\"/>\n" +
            "<marker id=\"KPDX\" label=\"Portland Airport\" lat=\"45.5908\" lng=\"-122.6\" wind=\"6\" gust=\"0\" angle=\"90\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"PRTRD\" label=\"Rodgers Marine\" lat=\"45.6011\" lng=\"-122.629\" wind=\"8\" gust=\"8\" angle=\"292\" timestamp=\"1323875640\"/>\n" +
            "<marker id=\"CAMAS\" label=\"Camas\" lat=\"45.5989\" lng=\"-122.431\" wind=\"1\" gust=\"2\" angle=\"0\" timestamp=\"1323875820\"/>\n" +
            "<marker id=\"KTTD\" label=\"Troutdale Airport\" lat=\"45.5511\" lng=\"-122.409\" wind=\"3\" gust=\"0\" angle=\"0\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"RRWO3\" label=\"Rooster Rock\" lat=\"45.54\" lng=\"-122.28\" wind=\"21\" gust=\"28\" angle=\"225\" timestamp=\"1463157900\"/>\n" +
            "<marker id=\"C2664\" label=\"Corbett\" lat=\"45.5313\" lng=\"-122.292\" wind=\"21\" gust=\"24\" angle=\"247\" timestamp=\"1463158200\"/>\n" +
            "<marker id=\"TBELL\" label=\"Cape Horn\" lat=\"45.569\" lng=\"-122.203\" wind=\"1\" gust=\"10\" angle=\"270\" timestamp=\"1463154300\"/>\n" +
            "<marker id=\"KWAWASHO9\" label=\"Multnomah Falls\" lat=\"45.59\" lng=\"-122.15\" wind=\"6\" gust=\"8\" angle=\"90\" timestamp=\"1441306997\"/>\n" +
            "<marker id=\"LKSO3\" label=\"Cascade Locks\" lat=\"45.6694\" lng=\"-121.882\" wind=\"7\" gust=\"12\" angle=\"90\" timestamp=\"1463155680\"/>\n" +
            "<marker id=\"BNDW\" label=\"Bonneville Dam\" lat=\"45.6478\" lng=\"-121.931\" wind=\"2\" gust=\"7\" angle=\"22\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"OR004\" label=\"Stevenson\" lat=\"45.6941\" lng=\"-121.88\" wind=\"7\" gust=\"10\" angle=\"292\" timestamp=\"1410381720\"/>\n" +
            "<marker id=\"UP073\" label=\"Viento\" lat=\"45.7049\" lng=\"-121.616\" wind=\"14\" gust=\"18\" angle=\"202\" timestamp=\"1330038000\"/>\n" +
            "<marker id=\"KORHOODR1\" label=\"Hood River City\" lat=\"45.7\" lng=\"-121.54\" wind=\"5\" gust=\"14\" angle=\"112\" timestamp=\"1458761397\"/>\n" +
            "<marker id=\"KQHD\" label=\"Hood River Airport\" lat=\"45.6726\" lng=\"-121.536\" wind=\"3\" gust=\"0\" angle=\"337\" timestamp=\"1444153140\"/>\n" +
            "<marker id=\"HOXO\" label=\"Hood River 2\" lat=\"45.6844\" lng=\"-121.518\" wind=\"2\" gust=\"6\" angle=\"135\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"KORMOSIE1\" label=\"Mosier\" lat=\"45.67\" lng=\"-121.4\" wind=\"6\" gust=\"6\" angle=\"270\" timestamp=\"1372446660\"/>\n" +
            "<marker id=\"UP048\" label=\"Rowena\" lat=\"45.6773\" lng=\"-121.283\" wind=\"5\" gust=\"5\" angle=\"22\" timestamp=\"1463155200\"/>\n" +
            "<marker id=\"KWALYLE4\" label=\"High Prairie\" lat=\"45.72\" lng=\"-121.17\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1458761476\"/>\n" +
            "<marker id=\"KWAMURDO3\" label=\"Dougs Beach\" lat=\"45.66\" lng=\"-121.2\" wind=\"8\" gust=\"12\" angle=\"112\" timestamp=\"1458761399\"/>\n" +
            "<marker id=\"WSBO3\" label=\"Wasco Butte\" lat=\"45.6167\" lng=\"-121.335\" wind=\"5\" gust=\"9\" angle=\"292\" timestamp=\"1463156040\"/>\n" +
            "<marker id=\"KDLS\" label=\"The Dalles\" lat=\"45.6186\" lng=\"-121.167\" wind=\"5\" gust=\"0\" angle=\"45\" timestamp=\"1463140380\"/>\n" +
            "<marker id=\"ODT16\" label=\"Celilo\" lat=\"45.6434\" lng=\"-120.979\" wind=\"1\" gust=\"0\" angle=\"90\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"GLDND\" label=\"Maryhill Winery\" lat=\"45.6725\" lng=\"-120.881\" wind=\"3\" gust=\"5\" angle=\"270\" timestamp=\"1323875640\"/>\n" +
            "<marker id=\"TMARY\" label=\"Maryhill\" lat=\"45.6981\" lng=\"-120.815\" wind=\"9\" gust=\"12\" angle=\"0\" timestamp=\"1463155800\"/>\n" +
            "<marker id=\"MWQRU\" label=\"Rufus\" lat=\"45.73\" lng=\"-120.65\" wind=\"35\" gust=\"43\" angle=\"135\" timestamp=\"1329948060\"/>\n" +
            "<marker id=\"ODT17\" label=\"John Day\" lat=\"45.7293\" lng=\"-120.652\" wind=\"6\" gust=\"0\" angle=\"225\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"HSURF\" label=\"Roosevelt\" lat=\"45.744\" lng=\"-120.218\" wind=\"6\" gust=\"8\" angle=\"0\" timestamp=\"1416238200\"/>\n" +
            "<marker id=\"MWQAR\" label=\"Arlington\" lat=\"45.7126\" lng=\"-120.199\" wind=\"1\" gust=\"0\" angle=\"45\" timestamp=\"1463160840\"/>\n" +
            "<marker id=\"UP143\" label=\"Boardman\" lat=\"45.8334\" lng=\"-119.771\" wind=\"4\" gust=\"6\" angle=\"247\" timestamp=\"1463156100\"/>\n" +
            "<marker id=\"UMTO3\" label=\"Umatilla\" lat=\"45.9167\" lng=\"-119.567\" wind=\"11\" gust=\"21\" angle=\"202\" timestamp=\"1463156940\"/>\n" +
            "<marker id=\"KHRI\" label=\"Hermiston\" lat=\"45.8258\" lng=\"-119.261\" wind=\"6\" gust=\"0\" angle=\"225\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KPSC\" label=\"Pasco\" lat=\"46.2697\" lng=\"-119.117\" wind=\"14\" gust=\"0\" angle=\"157\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"C6208\" label=\"Long Beach\" lat=\"46.4434\" lng=\"-124.053\" wind=\"0\" gust=\"0\" angle=\"292\" timestamp=\"1283960160\"/>\n" +
            "<marker id=\"KWACATHL3\" label=\"Jones Beach\" lat=\"46.1661\" lng=\"-123.393\" wind=\"3\" gust=\"4\" angle=\"315\" timestamp=\"1458761341\"/>\n" +
            "<marker id=\"46029\" label=\"Buoy 46029\" lat=\"46.1\" lng=\"-124.5\" wind=\"0\" gust=\"0\" angle=\"1\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"K82S\" label=\"Cape Disappointment\" lat=\"46.2833\" lng=\"-124.052\" wind=\"10\" gust=\"0\" angle=\"315\" timestamp=\"1302631800\"/>\n" +
            "<marker id=\"3CLO3\" label=\"Clatsop Spit\" lat=\"46.22\" lng=\"-124\" wind=\"3\" gust=\"0\" angle=\"67\" timestamp=\"1463154300\"/>\n" +
            "<marker id=\"KAST\" label=\"Astoria\" lat=\"46.1569\" lng=\"-123.882\" wind=\"3\" gust=\"0\" angle=\"1\" timestamp=\"1463150220\"/>\n" +
            "<marker id=\"ODT76\" label=\"Megler Bridge\" lat=\"46.1951\" lng=\"-123.852\" wind=\"6\" gust=\"0\" angle=\"45\" timestamp=\"1461061860\"/>\n" +
            "<marker id=\"KORASTOR15\" label=\"Maritime Museum\" lat=\"46.19\" lng=\"-123.82\" wind=\"0\" gust=\"0\" angle=\"225\" timestamp=\"1328621699\"/>\n" +
            "<marker id=\"asto3\" label=\"Astoria Buoy\" lat=\"46.208\" lng=\"-123.767\" wind=\"2\" gust=\"3\" angle=\"90\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"001D0A00287E\" label=\"Warrenton\" lat=\"46.1731\" lng=\"-123.916\" wind=\"4\" gust=\"4\" angle=\"45\" timestamp=\"1432237980\"/>\n" +
            "<marker id=\"KORGEARH1\" label=\"Gearhart\" lat=\"46.03\" lng=\"-123.93\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1463159940\"/>\n" +
            "<marker id=\"KORSEASI1\" label=\"The Cove\" lat=\"45.98\" lng=\"-123.94\" wind=\"2\" gust=\"2\" angle=\"112\" timestamp=\"1463160096\"/>\n" +
            "<marker id=\"AT709\" label=\"Oceanside\" lat=\"45.4603\" lng=\"-123.969\" wind=\"3\" gust=\"5\" angle=\"45\" timestamp=\"1463158260\"/>\n" +
            "<marker id=\"OR008\" label=\"Manzanita\" lat=\"45.7204\" lng=\"-123.934\" wind=\"5\" gust=\"4\" angle=\"135\" timestamp=\"1390940580\"/>\n" +
            "<marker id=\"CNHNB\" label=\"Cannon Beach\" lat=\"45.8719\" lng=\"-123.961\" wind=\"4\" gust=\"5\" angle=\"315\" timestamp=\"1323875820\"/>\n" +
            "<marker id=\"AS469\" label=\"Rockaway Beach\" lat=\"45.6068\" lng=\"-123.935\" wind=\"1\" gust=\"1\" angle=\"180\" timestamp=\"1463099580\"/>\n" +
            "<marker id=\"TLBO3\" label=\"Garibaldi\" lat=\"45.555\" lng=\"-123.912\" wind=\"19\" gust=\"21\" angle=\"180\" timestamp=\"1435100760\"/>\n" +
            "<marker id=\"GARO3\" label=\"Garibaldi\" lat=\"45.57\" lng=\"-123.95\" wind=\"14\" gust=\"0\" angle=\"157\" timestamp=\"1462462200\"/>\n" +
            "<marker id=\"AS612\" label=\"Cape Meares\" lat=\"45.4661\" lng=\"-123.921\" wind=\"10\" gust=\"10\" angle=\"22\" timestamp=\"1463158020\"/>\n" +
            "<marker id=\"KOROCEAN2\" label=\"Oceanside\" lat=\"45.43\" lng=\"-123.94\" wind=\"0\" gust=\"4\" angle=\"90\" timestamp=\"1252783710\"/>\n" +
            "<marker id=\"AP682\" label=\"Road's End\" lat=\"45.0108\" lng=\"-124.006\" wind=\"2\" gust=\"7\" angle=\"157\" timestamp=\"1463111340\"/>\n" +
            "<marker id=\"AR659\" label=\"Lincoln City\" lat=\"44.977\" lng=\"-124.009\" wind=\"2\" gust=\"5\" angle=\"180\" timestamp=\"1463017740\"/>\n" +
            "<marker id=\"C9821\" label=\"Gleneden Beach\" lat=\"44.8647\" lng=\"-124.042\" wind=\"5\" gust=\"7\" angle=\"22\" timestamp=\"1463157900\"/>\n" +
            "<marker id=\"AR160\" label=\"Cape Foulweather\" lat=\"44.7562\" lng=\"-124.05\" wind=\"45\" gust=\"203\" angle=\"180\" timestamp=\"1383428940\"/>\n" +
            "<marker id=\"AS191\" label=\"Pacific City\" lat=\"45.1836\" lng=\"-123.935\" wind=\"1\" gust=\"3\" angle=\"45\" timestamp=\"1463102460\"/>\n" +
            "<marker id=\"NWPO3\" label=\"Newport\" lat=\"44.61\" lng=\"-124.07\" wind=\"2\" gust=\"3\" angle=\"67\" timestamp=\"1463151600\"/>\n" +
            "<marker id=\"KONP\" label=\"Newport Airport\" lat=\"44.5803\" lng=\"-124.058\" wind=\"3\" gust=\"0\" angle=\"45\" timestamp=\"1463150100\"/>\n" +
            "<marker id=\"FCGO3\" label=\"Florence\" lat=\"44.01\" lng=\"-124.12\" wind=\"6\" gust=\"0\" angle=\"45\" timestamp=\"1463156400\"/>\n" +
            "<marker id=\"CARO3\" label=\"Cape Arago\" lat=\"43.34\" lng=\"-124.38\" wind=\"9\" gust=\"13\" angle=\"337\" timestamp=\"1356620400\"/>\n" +
            "<marker id=\"KORBANDO2\" label=\"Bandon\" lat=\"43.12\" lng=\"-124.43\" wind=\"9\" gust=\"16\" angle=\"22\" timestamp=\"1458760952\"/>\n" +
            "<marker id=\"KOTH\" label=\"North Bend\" lat=\"43.4167\" lng=\"-124.25\" wind=\"8\" gust=\"0\" angle=\"90\" timestamp=\"1463154900\"/>\n" +
            "<marker id=\"K92S\" label=\"Cape Blanco\" lat=\"42.8333\" lng=\"-124.567\" wind=\"9\" gust=\"0\" angle=\"315\" timestamp=\"1463157000\"/>\n" +
            "<marker id=\"FPRO3\" label=\"Flynn Prairie\" lat=\"42.3956\" lng=\"-124.379\" wind=\"5\" gust=\"9\" angle=\"0\" timestamp=\"1463155980\"/>\n" +
            "<marker id=\"K4S1\" label=\"Gold Beach Airport\" lat=\"42.416\" lng=\"-124.427\" wind=\"5\" gust=\"0\" angle=\"22\" timestamp=\"1463133540\"/>\n" +
            "<marker id=\"ODT69\" label=\"Rogue River Bridge\" lat=\"42.4289\" lng=\"-124.413\" wind=\"5\" gust=\"0\" angle=\"90\" timestamp=\"1463157060\"/>\n" +
            "<marker id=\"KORBROOK6\" label=\"Pistol River\" lat=\"42.26\" lng=\"-124.4\" wind=\"4\" gust=\"5\" angle=\"225\" timestamp=\"1261458820\"/>\n" +
            "<marker id=\"KCEC\" label=\"Crescent City\" lat=\"41.7803\" lng=\"-124.237\" wind=\"5\" gust=\"0\" angle=\"45\" timestamp=\"1463154960\"/>\n" +
            "<marker id=\"46027\" label=\"St. Georges Buoy\" lat=\"41.9\" lng=\"-124.4\" wind=\"4\" gust=\"7\" angle=\"22\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"46050\" label=\"Buoy 46050\" lat=\"44.6\" lng=\"-124.5\" wind=\"2\" gust=\"4\" angle=\"90\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"46015\" label=\"Buoy 46015\" lat=\"42.7\" lng=\"-124.8\" wind=\"4\" gust=\"4\" angle=\"337\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"46089\" label=\"Tillamook Buoy\" lat=\"45.9\" lng=\"-125.8\" wind=\"2\" gust=\"4\" angle=\"135\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"46005\" label=\"Buoy 46005\" lat=\"46\" lng=\"-131\" wind=\"7\" gust=\"9\" angle=\"247\" timestamp=\"1463154600\"/>\n" +
            "<marker id=\"KSLE\" label=\"Salem\" lat=\"44.9078\" lng=\"-122.995\" wind=\"5\" gust=\"0\" angle=\"337\" timestamp=\"1463151360\"/>\n" +
            "<marker id=\"RNFO3\" label=\"Round Mountain\" lat=\"43.7664\" lng=\"-121.723\" wind=\"7\" gust=\"14\" angle=\"292\" timestamp=\"1463155560\"/>\n" +
            "<marker id=\"KEUG\" label=\"Eugene\" lat=\"44.1333\" lng=\"-123.214\" wind=\"6\" gust=\"0\" angle=\"22\" timestamp=\"1463154840\"/>\n" +
            "<marker id=\"C0799\" label=\"Ocean Park\" lat=\"46.4778\" lng=\"-124.05\" wind=\"1\" gust=\"2\" angle=\"112\" timestamp=\"1463158080\"/>\n" +
            "<marker id=\"OR010\" label=\"Sunset Beach\" lat=\"46.1189\" lng=\"-123.945\" wind=\"1\" gust=\"1\" angle=\"202\" timestamp=\"1278488578\"/>\n" +
            "<marker id=\"AP059\" label=\"Ridgefield\" lat=\"45.8033\" lng=\"-122.702\" wind=\"1\" gust=\"4\" angle=\"135\" timestamp=\"1463158080\"/>\n" +
            "<marker id=\"KORPORTL18\" label=\"Rocky Butte\" lat=\"45.54\" lng=\"-122.57\" wind=\"5\" gust=\"8\" angle=\"202\" timestamp=\"1311517382\"/>\n" +
            "<marker id=\"PORO3\" label=\"Port Orford\" lat=\"42.74\" lng=\"-124.497\" wind=\"6\" gust=\"7\" angle=\"22\" timestamp=\"1463156640\"/>\n" +
            "<marker id=\"WOTW02\" label=\"White Salmon\" lat=\"45.7355\" lng=\"-121.49\" wind=\"1\" gust=\"5\" angle=\"45\" timestamp=\"1463160027\"/>\n" +
            "<marker id=\"KCZK\" label=\"Cascade Locks State\" lat=\"45.6667\" lng=\"-121.883\" wind=\"13\" gust=\"21\" angle=\"22\" timestamp=\"1380581100\"/>\n" +
            "<marker id=\"KWAANDER1\" label=\"Wapato Rd\" lat=\"47.17\" lng=\"-122.7\" wind=\"0\" gust=\"0\" angle=\"0\" timestamp=\"1389043707\"/>\n" +
            "<marker id=\"KWATACOM35\" label=\"Sunset Beach\" lat=\"47.22\" lng=\"-122.57\" wind=\"2\" gust=\"5\" angle=\"180\" timestamp=\"1332028608\"/>\n" +
            "<marker id=\"KWASTANW8\" label=\"Livingston Bay\" lat=\"48.24\" lng=\"-122.44\" wind=\"23\" gust=\"23\" angle=\"292\" timestamp=\"1458761421\"/>\n" +
            "<marker id=\"UWOTWANOH\" label=\"Twanoh Buoy\" lat=\"47.375\" lng=\"-123.01\" wind=\"6\" gust=\"0\" angle=\"180\" timestamp=\"1463159258\"/>\n" +
            "<marker id=\"UWOHOODSPORT\" label=\"Hoodsport Buoy\" lat=\"47.4218\" lng=\"-123.113\" wind=\"6\" gust=\"0\" angle=\"180\" timestamp=\"1463159258\"/>\n" +
            "<marker id=\"UWODABOB\" label=\"Dabob Bay Buoy\" lat=\"47.8034\" lng=\"-122.803\" wind=\"6\" gust=\"0\" angle=\"180\" timestamp=\"1463159258\"/>\n" +
            "<marker id=\"UWOHANSVILLE\" label=\"North Buoy\" lat=\"47.907\" lng=\"-122.627\" wind=\"15\" gust=\"17\" angle=\"135\" timestamp=\"1462317761\"/>\n" +
            "<marker id=\"UWOPTWELLS\" label=\"Point Wells Buoy\" lat=\"47.761\" lng=\"-122.397\" wind=\"6\" gust=\"0\" angle=\"180\" timestamp=\"1463159258\"/>\n" +
            "<marker id=\"UWOCARRINLET\" label=\"Carr Inlet Buoy\" lat=\"47.28\" lng=\"-122.73\" wind=\"5\" gust=\"5\" angle=\"22\" timestamp=\"1463159509\"/>\n" +
            "<marker id=\"OSOSNO\" label=\"Snoqualmie Pass\" lat=\"47.4321\" lng=\"-121.43\" wind=\"8\" gust=\"10\" angle=\"315\" timestamp=\"1416524400\"/>\n" +
            "<marker id=\"KWAKIRKL23\" label=\"Lake Washington North\" lat=\"47.72\" lng=\"-122.26\" wind=\"4\" gust=\"0\" angle=\"292\" timestamp=\"1458753140\"/>\n" +
            "<marker id=\"OSOALP\" label=\"Alpental Denny Mountain\" lat=\"47.4367\" lng=\"-121.472\" wind=\"11\" gust=\"18\" angle=\"202\" timestamp=\"1402858800\"/>\n" +
            "<marker id=\"OSOMHM\" label=\"Mt Hood Meadows\" lat=\"45.3458\" lng=\"-121.672\" wind=\"6\" gust=\"13\" angle=\"45\" timestamp=\"1416524400\"/>\n" +
            "<marker id=\"OSOTMB\" label=\"Timberline Base\" lat=\"45.3308\" lng=\"-121.712\" wind=\"2\" gust=\"7\" angle=\"135\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOTIM\" label=\"Timberline Upper\" lat=\"45.3372\" lng=\"-121.709\" wind=\"4\" gust=\"13\" angle=\"337\" timestamp=\"1416520800\"/>\n" +
            "<marker id=\"OSOGVT\" label=\"Mt Hood Ski Bowl\" lat=\"45.294\" lng=\"-121.785\" wind=\"4\" gust=\"9\" angle=\"315\" timestamp=\"1402520400\"/>\n" +
            "<marker id=\"OSOWP9\" label=\"Cutthroat Ridge\" lat=\"48.5325\" lng=\"-120.647\" wind=\"12\" gust=\"14\" angle=\"45\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOMAZ\" label=\"Mazama\" lat=\"48.5662\" lng=\"-120.453\" wind=\"0\" gust=\"1\" angle=\"90\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOSK9\" label=\"Stevens Pass 1\" lat=\"47.7238\" lng=\"-121.084\" wind=\"0\" gust=\"3\" angle=\"90\" timestamp=\"1391025600\"/>\n" +
            "<marker id=\"OSOSTS\" label=\"Stevens Pass 2\" lat=\"47.7153\" lng=\"-121.1\" wind=\"33\" gust=\"34\" angle=\"0\" timestamp=\"1415127600\"/>\n" +
            "<marker id=\"OSOLAK\" label=\"Lake Wenatchee\" lat=\"47.8397\" lng=\"-120.757\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOLAK\" label=\"Tumwater Canyon\" lat=\"47.7028\" lng=\"-120.747\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSODIR\" label=\"Dirty Face\" lat=\"47.8397\" lng=\"-120.757\" wind=\"17\" gust=\"24\" angle=\"67\" timestamp=\"1416517200\"/>\n" +
            "<marker id=\"OSOLAK\" label=\"Lake Wenatchee\" lat=\"47.8397\" lng=\"-120.757\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOLAK\" label=\"Tumwater Canyon\" lat=\"47.7028\" lng=\"-120.747\" wind=\"0\" gust=\"0\" angle=\"67\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOHUR\" label=\"Hurricane Ridge\" lat=\"47.9704\" lng=\"-123.498\" wind=\"9\" gust=\"15\" angle=\"337\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOHUR\" label=\"Mission Ridge Summit\" lat=\"47.285\" lng=\"-120.414\" wind=\"9\" gust=\"15\" angle=\"337\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOHUR\" label=\"Hurricane Ridge\" lat=\"47.9704\" lng=\"-123.498\" wind=\"9\" gust=\"15\" angle=\"337\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOHUR\" label=\"Mission Ridge Summit\" lat=\"47.285\" lng=\"-120.414\" wind=\"9\" gust=\"15\" angle=\"337\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOCMT\" label=\"Crystal Mt Summit\" lat=\"46.9335\" lng=\"-121.477\" wind=\"27\" gust=\"41\" angle=\"45\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOSUN\" label=\"Mt Rainier Sunrise Knob\" lat=\"46.9097\" lng=\"-121.651\" wind=\"4\" gust=\"8\" angle=\"0\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOPVC\" label=\"Paradise Wind Site\" lat=\"46.7874\" lng=\"-121.737\" wind=\"9\" gust=\"13\" angle=\"90\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOMUR\" label=\"Mt Rainier Camp Muir\" lat=\"46.8117\" lng=\"-121.716\" wind=\"38\" gust=\"46\" angle=\"112\" timestamp=\"1416520800\"/>\n" +
            "<marker id=\"OSOWPS\" label=\"White Pass Wind\" lat=\"46.6404\" lng=\"-121.404\" wind=\"13\" gust=\"24\" angle=\"67\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOMSH\" label=\"Coldwater Lake\" lat=\"46.2844\" lng=\"-122.242\" wind=\"0\" gust=\"5\" angle=\"270\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"OSOMTB\" label=\"Mt Baker\" lat=\"48.8629\" lng=\"-121.677\" wind=\"16\" gust=\"23\" angle=\"22\" timestamp=\"1416528000\"/>\n" +
            "<marker id=\"KELN\" label=\"Ellensburg Bowers Field\" lat=\"47.0336\" lng=\"-120.529\" wind=\"3\" gust=\"0\" angle=\"112\" timestamp=\"1463154780\"/>\n" +
            "<marker id=\"KWACAMAN3\" label=\"Indian Beach\" lat=\"48.17\" lng=\"-122.52\" wind=\"5\" gust=\"12\" angle=\"0\" timestamp=\"1458761341\"/>\n" +
            "<marker id=\"KORS\" label=\"Eastsound\" lat=\"48.7081\" lng=\"-122.91\" wind=\"7\" gust=\"0\" angle=\"157\" timestamp=\"1463157300\"/>\n" +
            "</markers>";

    static public void parseRawSensorData(String rawSensorData, List<WindSensor> windSensors) {
        // TODO Test data
        /*
        <markers>
<marker id="WOTW07" label="Hat Island" lat="48.0194" lng="-122.334" wind="1" gust="3" angle="225" timestamp="1462988635"/>
<marker id="WA010" label="Locust Beach" lat="48.7767" lng="-122.562" wind="3" gust="5" angle="337" timestamp="1462988340"/>
<marker id="PBFW1" label="Padilla Bay" lat="48.4639" lng="-122.468" wind="2" gust="4" angle="315" timestamp="1462984200"/>
<marker id="KNUW" label="Whidbey Island" lat="48.3492" lng="-122.651" wind="7" gust="0" angle="135" timestamp="1462985760"/>
<marker id="WA001" label="Jetty Island" lat="48.0035" lng="-122.228" wind="6" gust="3" angle="90" timestamp="1409602200"/>

<marker id="KWAFREEL8" label="Useless Bay" lat="47.99" lng="-122.51" wind="3" gust="7" angle="180" timestamp="1441839360"/>
<marker id="KWAFREEL6" label="Mutiny Bay" lat="48.01" lng="-122.56" wind="15" gust="15" angle="315" timestamp="1436731836"/>
<marker id="KUIL" label="Quillayute Airport" lat="47.9375" lng="-124.555" wind="8" gust="0" angle="45" timestamp="1463093580"/>
<marker id="KPAE" label="Everett: Snohomish County Airport" lat="47.9231" lng="-122.283" wind="6" gust="0" angle="112" timestamp="1463093580"/>
<marker id="WA002" label="Point No Point" lat="47.9167" lng="-122.533" wind="3" gust="0" angle="0" timestamp="1459895122"/>

        *
         */
        //WindSensor windSensor = new WindSensor(lat, lon, title, dir, speed)
        windSensors.add(new WindSensor((float) 48.0194, (float) -122.334, "Hat Island", 225, 10));
        windSensors.add(new WindSensor((float) 48.7767, (float) -122.562, "Locust Beach", 337, 15));
        windSensors.add(new WindSensor((float) 48.4639, (float) -122.468, "Padilla Bay", 315, 20));
        windSensors.add(new WindSensor((float) 48.3492, (float) -122.651, "Whidbey Island", 135, 25));
        windSensors.add(new WindSensor((float) 48.0035, (float) -122.228, "Jetty Island", 90, 30));

        windSensors.add(new WindSensor((float) 47.99, (float) -122.51, "Useless Bay", 180, 22));
        windSensors.add(new WindSensor((float) 48.01, (float) -122.56, "Mutiny Bay", 315, 24));
        windSensors.add(new WindSensor((float) 47.9375, (float) -124.555, "Quillayute Airport", 45, 26));
        windSensors.add(new WindSensor((float) 47.9231, (float) -122.283, "Everett: Snohomish County Airport", 0, 28));
        windSensors.add(new WindSensor((float) 47.9167, (float) -122.533, "Point No Point", 0, 30));

    }

    static public void parseRawSensorData2(String rawSensorData, List<WindSensor> windSensors)
            throws XmlPullParserException, IOException {
        try {
            // TODO Test
            long startTime = System.nanoTime();

            rawSensorData = WINDSENSOR_TEST_DATA;

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(rawSensorData));
            parser.nextTag();
            readFeed(windSensors, parser);
            long endTime = System.nanoTime();
            Log.d(TAG, "Time to parse " + (endTime - startTime));
        } finally {

        }
    }

    private static final String ns = null;

    static void readFeed(List<WindSensor> windSensors, XmlPullParser parser)  throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "markers");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the marker tag
            if (name.equals("marker")) {
                windSensors.add(readMarker(parser));
            } else {
                skip(parser);
            }
        }
    }

    static private WindSensor readMarker(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        //parser.require(XmlPullParser.START_TAG, ns, "marker");
        String lat = null;
        String lng = null;
        String title = null;
        String speed = null;
        String direction = null;
        String name = parser.getName();

//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
            name = parser.getName();
            if (name.equals("marker")) {
                //title = readTitle(parser);
                lat = parser.getAttributeValue(null, "lat");
                lng = parser.getAttributeValue(null, "lng");
                title = parser.getAttributeValue(null, "label");
                speed = parser.getAttributeValue(null, "wind");
                direction = parser.getAttributeValue(null, "angle");
            } else {
                //skip(parser);
            }
        parser.next();
//        }

        return new WindSensor(Float.parseFloat(lat), Float.parseFloat(lng), title,
                Float.parseFloat(direction), Float.parseFloat(speed));
    }

    static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
