package org.opensrp.repository.postgres;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.opensrp.domain.LocationDetail;
import org.smartregister.domain.Client;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.LocationProperty.PropertyStatus;
import org.opensrp.domain.LocationTagMap;
import org.smartregister.domain.Geometry.GeometryType;
import org.opensrp.domain.StructureDetails;
import org.opensrp.domain.StructureCount;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.LocationTagRepository;
import org.opensrp.search.LocationSearchBean;
import org.opensrp.search.LocationSearchBean.OrderByType;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.utils.PropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class LocationRepositoryTest extends BaseRepositoryTest {
	
	private String parentJson = "{\"id\": \"3366\",\"locationTags\": [{\"active\":true,\"name\":\"District\",\"description\":\"description\",\"id\":1}], \"type\": \"Feature\", \"geometry\": {\"type\": \"MultiPolygon\", \"coordinates\": [[[[32.60019625316408, -14.16712789883206], [32.60026392129685, -14.167200170670068], [32.600326068380916, -14.167277018364924], [32.60038237040833, -14.167358041272504], [32.60043253384264, -14.167442816981005], [32.600461865346055, -14.167501855260806], [32.600467305754925, -14.167504082164154], [32.6005567791152, -14.167548365892056], [32.60064274437109, -14.16759883363829], [32.60072475334385, -14.167655222291442], [32.600802378479834, -14.167717237871212], [32.60087521507938, -14.167784557061506], [32.60094288340667, -14.167856828895705], [32.60100503066945, -14.167933676586241], [32.6010613328587, -14.168014699489389], [32.60111149643739, -14.16809947519317], [32.601155259871, -14.168187561720227], [32.60119239499158, -14.168278499831782], [32.60122270818726, -14.168371815421606], [32.601246041411336, -14.168467021987961], [32.60126227300679, -14.16856362316997], [32.60127131834104, -14.168661115335109], [32.601273130246504, -14.168758990205273], [32.60126809520835, -14.168849611326149], [32.601268746132114, -14.168884772675423], [32.60126331515021, -14.16898251997554], [32.601250669585276, -14.1690796300968], [32.6012308753564, -14.169175596751185], [32.60120403565332, -14.169269919610993], [32.6011702903973, -14.169362106918296], [32.60112981551378, -14.169451678048457], [32.60108282201332, -14.169538166015567], [32.60102955489338, -14.169621119908012], [32.60097029185993, -14.169700107238382], [32.60090534188031, -14.169774716199182], [32.6008350435718, -14.169844557809366], [32.600817649001606, -14.169859510027148], [32.60076264386304, -14.169914157803586], [32.60068736370561, -14.169978867936662], [32.60060749419761, -14.170038109219828], [32.600559934780954, -14.170068364123377], [32.60056014202427, -14.170070597813773], [32.6005619539411, -14.170168472672398], [32.60055652292868, -14.170266219962112], [32.60054387729263, -14.170363330073084], [32.600524082952795, -14.170459296717066], [32.60049724309874, -14.170553619566872], [32.600463497653145, -14.170645806864396], [32.60042302254208, -14.17073537798495], [32.60037602877764, -14.170821865942909], [32.600344492416745, -14.170870977832461], [32.60034438539213, -14.170871496706125], [32.60031754548958, -14.170965819552745], [32.60028379998311, -14.171058006847087], [32.600243324799, -14.171147577964515], [32.600196330949636, -14.17123406591946], [32.600143063434295, -14.171317019800309], [32.60008379996071, -14.171396007119537], [32.600018849498774, -14.171470616069879], [32.59994855066821, -14.171540457670288], [32.59987326997469, -14.17160516779455], [32.59979339989791, -14.171664409069702], [32.59970935684628, -14.171717872634868], [32.59964225969093, -14.17175411050408], [32.59958840035244, -14.171794059057728], [32.599504357253004, -14.171847522622325], [32.59941657934149, -14.171894929738581], [32.59932552425658, -14.17193603324409], [32.59923166672291, -14.171970618840756], [32.599135496077615, -14.171998506212216], [32.599037513717285, -14.172019549964542], [32.59893823048531, -14.17203364038323], [32.598838164007354, -14.172040704006005], [32.59873783599283, -14.172040704006005], [32.5986377695147, -14.17203364038323], [32.598538486282784, -14.172019549964542], [32.59844050392257, -14.171998506212216], [32.59836476694522, -14.171976544154969], [32.598302157782705, -14.172016372605977], [32.598214379806386, -14.172063779721551], [32.59812332465395, -14.172104883226435], [32.59802946705104, -14.172139468822703], [32.59793329633447, -14.172167356193764], [32.59783531390183, -14.17218839994575], [32.59773603059636, -14.172202490364267], [32.59763596404451, -14.172209553987042], [32.597535635955744, -14.172209553987042], [32.59743556940372, -14.172202490364267], [32.59735870544443, -14.17219158172912], [32.59734235282223, -14.172210366013356], [32.597272053763895, -14.172280207609443], [32.59721759844722, -14.172327016406367], [32.59719788529895, -14.172349660918595], [32.59712758619781, -14.172419502513943], [32.59705230521461, -14.172484212633432], [32.59697243483037, -14.172543453904321], [32.59691580121147, -14.17257948095516], [32.596914419626664, -14.172580505700747], [32.596830376237726, -14.172633969261877], [32.59674259802403, -14.172681376375007], [32.59665154262529, -14.172722479877844], [32.59655768476852, -14.172757065472238], [32.59646151379189, -14.17278495284199], [32.59636353109419, -14.172805996592896], [32.59626424752013, -14.172820087010617], [32.596164180697365, -14.172827150632937], [32.596063852337345, -14.172827150632937], [32.595963785514634, -14.172820087010617], [32.595864501940746, -14.172805996592896], [32.59576651924305, -14.17278495284199], [32.595670348266246, -14.172757065472238], [32.59562983666279, -14.17274213738909], [32.59557352665137, -14.17277254919497], [32.595482471216194, -14.172813652697524], [32.59538861332191, -14.172848238291575], [32.595346903973216, -14.172860333037871], [32.59534239579221, -14.172862368083766], [32.59524853787786, -14.172896953677704], [32.59515236684228, -14.172924841047173], [32.595054384084555, -14.17294588479774], [32.59495510044973, -14.172959975215402], [32.594855033565686, -14.172967038837665], [32.59475470514422, -14.172967038837665], [32.59465463826023, -14.172959975215402], [32.594555354625406, -14.17294588479774], [32.59445737186763, -14.172924841047173], [32.59442506589232, -14.172915473063707], [32.59433007182053, -14.172915473063707], [32.59423000495905, -14.17290840944133], [32.594130721346794, -14.172894319023781], [32.59403273861107, -14.172873275273044], [32.593936567597375, -14.172845387903461], [32.59384270970417, -14.172810802309353], [32.59375165427024, -14.1727696988068], [32.59366387602261, -14.172722291693898], [32.59357983260116, -14.172668828133279], [32.593499962173034, -14.172609586863073], [32.5934246811484, -14.172544876744151], [32.5934126306829, -14.172532904709552], [32.59335004540372, -14.17247910759323], [32.593279746284274, -14.172409265998223], [32.593214795555234, -14.172334657053792], [32.59315553183807, -14.172255669740649], [32.59310226410371, -14.172172715866392], [32.59305527006142, -14.172086227918097], [32.593014794710705, -14.171996656807604], [32.592981049065706, -14.171904469520424], [32.59295420905283, -14.171810146681306], [32.592934414595895, -14.17171418004801], [32.59292176888527, -14.171617069947953], [32.59291633784067, -14.171519322669322], [32.59291814976836, -14.171421447821611], [32.592927195211864, -14.171323955678812], [32.59294342700377, -14.17122735451903], [32.5929667605099, -14.171132147974616], [32.59299707407205, -14.171038832406278], [32.593034209641694, -14.170947894315642], [32.59307797360429, -14.17085980780882], [32.59312813778927, -14.170775032124592], [32.59318444065922, -14.170694009240147], [32.593246588673246, -14.170617161567233], [32.59331425781857, -14.170544889749747], [32.59338709529839, -14.17047757057497], [32.59346472137269, -14.170415555009466], [32.59354673133668, -14.17035916636939], [32.593632697631556, -14.170308698634809], [32.59372217207328, -14.17026441491714], [32.59381468818895, -14.17022654608763], [32.59390976364897, -14.170195289573538], [32.59400690278124, -14.17017080832937], [32.59410559915556, -14.17015322998668], [32.594108641289544, -14.170152907171083], [32.59410452416977, -14.170078807274818], [32.594104747347494, -14.170066751816304], [32.59409492477396, -14.170019130079421], [32.594082279157185, -14.169922019965721], [32.59407684815306, -14.169824272673221], [32.59407866006705, -14.169726397811754], [32.59408770544342, -14.169628905655198], [32.5941039371147, -14.169532304481717], [32.59412727044752, -14.169437097923888], [32.59415758378457, -14.169343782342306], [32.59419471907852, -14.16925284423888], [32.59423848271643, -14.169164757719555], [32.59428864652886, -14.169079982023387], [32.59434494898084, -14.168998959127517], [32.59440709653352, -14.168922111443745], [32.594474765176464, -14.16884983961597], [32.59454760211582, -14.168782520431701], [32.59462522761384, -14.168720504857387], [32.594707236969036, -14.168664116209353], [32.59479320262607, -14.168613648467609], [32.59488267640358, -14.168569364743629], [32.594975191832525, -14.168531495908722], [32.59507026658701, -14.168500239390308], [32.59516740499827, -14.168475758142618], [32.59526610063994, -14.168458179797423], [32.59529244876699, -14.168455383848364], [32.59529777359045, -14.168442344202102], [32.59534153707289, -14.16835425767692], [32.595391700707346, -14.168269481975013], [32.595448002959465, -14.16818845907363], [32.59551015029154, -14.168111611384685], [32.59557781869437, -14.16803933955202], [32.5956506553752, -14.167972020363091], [32.595728280597704, -14.16791000478474], [32.59578277701842, -14.167872533564658], [32.59578663074716, -14.167869454781108], [32.59586863979683, -14.167813066128982], [32.595870154342826, -14.167812176983087], [32.59588304190657, -14.167790397169766], [32.595939344040566, -14.167709374265083], [32.59600149124241, -14.167632526573069], [32.59606915950308, -14.167560254737506], [32.59614199603117, -14.167492935545964], [32.59621962109099, -14.167430919964941], [32.59630162998314, -14.167374531310939], [32.59638759515462, -14.16732406356368], [32.59647706842696, -14.167279779834924], [32.596569583333626, -14.167241910996037], [32.59666465755111, -14.1672106544741], [32.59676179541406, -14.16718617322385], [32.59686049049849, -14.167168594876781], [32.59696022826284, -14.167158011076763], [32.597060488729475, -14.167154477001755], [32.59716074919606, -14.167158011076763], [32.5972604869604, -14.167168594876781], [32.59731452694961, -14.167178219810861], [32.59732387518306, -14.167149442233491], [32.597361010120146, -14.167058504113466], [32.59740477333741, -14.166970417578053], [32.59745493666792, -14.166885641866255], [32.59751123857881, -14.166804618955549], [32.59757338553447, -14.16672777125768], [32.59764105352729, -14.166655499416716], [32.59771388976673, -14.16658818022], [32.597791514519024, -14.166526164634373], [32.59787352308643, -14.166469775975994], [32.597959487917244, -14.166419308225041], [32.59804896083539, -14.166375024492819], [32.59814147537559, -14.166337155650975], [32.59823654921649, -14.166305899126767], [32.598333686694666, -14.166281417874698], [32.59843238138813, -14.16626383952632], [32.598480894615356, -14.16625869146247], [32.59854368499213, -14.166247507971715], [32.59864342235414, -14.166236924170786], [32.59874368241662, -14.16623339009561], [32.59884394247893, -14.166236924170786], [32.598943679841106, -14.166247507971715], [32.59904237452752, -14.166265086320092], [32.59913951199871, -14.166289567572331], [32.59923458583284, -14.166320824096541], [32.5993271003665, -14.166358692938498], [32.599416573278056, -14.166402976670607], [32.59950253810285, -14.166453444421673], [32.599584546664396, -14.166509833080223], [32.599662171411175, -14.16657184866585], [32.59973500764527, -14.166639167862566], [32.59980267563332, -14.166711439703702], [32.59986482258454, -14.166788287401683], [32.59992112449134, -14.166869310312448], [32.59996455353569, -14.16694270512846], [32.60004579186107, -14.166998564054833], [32.600123416774004, -14.167060579638072], [32.60019625316408, -14.16712789883206]]]]}, \"properties\": {\"name\": \"MKB_5\", \"status\": \"Active\", \"version\": 0, \"parentId\": \"2953\", \"geographicLevel\": 2}, \"serverVersion\": 1542965231623}";
	
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
	        .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).serializeNulls().create();
	
	private Set<String> scripts = new HashSet<String>();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private LocationTagRepository locationTagRepository;
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("location.sql");
		scripts.add("structure.sql");
		scripts.add("location_tag.sql");
		scripts.add("plan.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		PhysicalLocation location = locationRepository.get("3734");
		assertNotNull(location);
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", location.getProperties().getUid());
		assertEquals("21", location.getProperties().getParentId());
		assertEquals("Intervention Unit", location.getProperties().getType());
		assertEquals(PropertyStatus.ACTIVE, location.getProperties().getStatus());
		assertEquals(1542378347104l, location.getServerVersion().longValue());
		
		JsonArray coordinates = location.getGeometry().getCoordinates().get(0).getAsJsonArray().get(0).getAsJsonArray();
		assertEquals(267, coordinates.size());
		
		JsonArray coordinate1 = coordinates.get(0).getAsJsonArray();
		assertEquals(32.59989007736522, coordinate1.get(0).getAsDouble(), 0);
		assertEquals(-14.167432040756012, coordinate1.get(1).getAsDouble(), 0);
		
		JsonArray coordinate67 = coordinates.get(66).getAsJsonArray();
		assertEquals(32.5988341383848, coordinate67.get(0).getAsDouble(), 0);
		assertEquals(-14.171814074659776, coordinate67.get(1).getAsDouble(), 0);
	}
	
	@Test
	public void testGetWithNullOrEmptyParams() {
		assertNull(locationRepository.get(""));
		
		assertNull(locationRepository.get(null));
		
	}
	
	@Test
	public void testGetNotExistingLocation() {
		assertNull(locationRepository.get("1212121"));
	}
	
	@Test
	public void testGetStructure() throws ParseException {
		PhysicalLocation structure = locationRepository.getStructure("90397", true);
		assertNotNull(structure);
		assertEquals("90397", structure.getId());
		
		assertEquals(GeometryType.POLYGON, structure.getGeometry().getType());
		assertFalse(structure.getGeometry().getCoordinates().isJsonNull());
		
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", structure.getProperties().getUid());
		assertEquals("21384443", structure.getProperties().getCode());
		assertEquals("Residential Structure", structure.getProperties().getType());
		assertEquals(PropertyStatus.ACTIVE, structure.getProperties().getStatus());
		assertEquals("3734", structure.getProperties().getParentId());
		assertNull(structure.getProperties().getName());
		assertEquals(5, structure.getProperties().getGeographicLevel());
		assertEquals(dateFormat.parse("2017-01-10"), structure.getProperties().getEffectiveStartDate());
		assertNull(structure.getProperties().getEffectiveEndDate());
		assertEquals(0, structure.getProperties().getVersion());
	}
	
	@Test
	public void testGetStructureWithNullOrEmptyParams() {
		assertNull(locationRepository.getStructure("", true));
		
		assertNull(locationRepository.getStructure(null, true));
		
	}
	
	@Test
	public void testGetStructureNotExistingLocation() {
		assertNull(locationRepository.getStructure("1212121", true));
	}
	
	@Test
	public void testAddLocation() {
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation physicalLocation = createLocation(uuid);
		physicalLocation.getProperties().setStatus(PropertyStatus.ACTIVE);
		locationRepository.add(physicalLocation);
		PhysicalLocation savedLocation = locationRepository.get("223232");
		
		assertNotNull(savedLocation);
		assertEquals("Feature", savedLocation.getType());
		
		assertNull(locationRepository.getStructure("223232", true));
		
	}
	
	@Test
	public void testAddLocationWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setJurisdiction(true);
		locationRepository.add(physicalLocation);
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testAddLocationExistingShouldNotChangeObject() {
		
		PhysicalLocation physicalLocation = locationRepository.get("3734");
		physicalLocation.getProperties().setName("MY Operational Area");
		physicalLocation.setJurisdiction(true);
		
		locationRepository.add(physicalLocation);
		
		physicalLocation = locationRepository.get("3734");
		assertNotEquals("MY Operational Area", physicalLocation.getProperties().getName());
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testAddStructure() {
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation physicalLocation = createStructure(uuid);
		
		locationRepository.add(physicalLocation);
		PhysicalLocation savedLocation = locationRepository.getStructure("121212", true);
		
		assertNotNull(savedLocation);
		assertEquals("Feature", savedLocation.getType());
		assertEquals(GeometryType.POLYGON, savedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.ACTIVE, savedLocation.getProperties().getStatus());
		assertEquals(uuid, savedLocation.getProperties().getUid());
		
		assertNull(locationRepository.get("121212"));
		
	}
	
	@Test
	public void testAddStructureWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationRepository.add(physicalLocation);
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testAddStructureExistingShouldNotChangeObject() {
		
		PhysicalLocation physicalLocation = locationRepository.getStructure("90397", true);
		physicalLocation.getProperties().setName("Mwangala Household");
		locationRepository.add(physicalLocation);
		
		physicalLocation = locationRepository.getStructure("90397", true);
		assertNotEquals("Mwangala Household", physicalLocation.getProperties().getName());
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testUpdateLocation() throws ParseException {
		PhysicalLocation physicalLocation = locationRepository.get("3734");
		assertEquals(0, physicalLocation.getProperties().getVersion());
		assertEquals(PropertyStatus.ACTIVE, physicalLocation.getProperties().getStatus());

		physicalLocation.getGeometry().setType(GeometryType.POLYGON);
		physicalLocation.getProperties().setStatus(PropertyStatus.NOT_ELIGIBLE);
		physicalLocation.getProperties().setGeographicLevel(3);
		
		Date effectiveStartDate = dateFormat.parse("2019-07-15");
		Date effectiveEndDate = dateFormat.parse("2020-07-15");
		physicalLocation.getProperties().setEffectiveStartDate(effectiveStartDate);
		physicalLocation.getProperties().setEffectiveEndDate(effectiveEndDate);
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);
		assertNull(locationRepository.get("3734"));
		PhysicalLocation updatedLocation = locationRepository.get("3734", true, 0);
		
		assertNotNull(updatedLocation);
		assertEquals(GeometryType.POLYGON, updatedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.NOT_ELIGIBLE, updatedLocation.getProperties().getStatus());
		assertEquals(3, updatedLocation.getProperties().getGeographicLevel());
		assertEquals(effectiveStartDate, updatedLocation.getProperties().getEffectiveStartDate());
		assertEquals(effectiveEndDate, updatedLocation.getProperties().getEffectiveEndDate());
		assertEquals(0, updatedLocation.getProperties().getVersion());
		
		assertNull(locationRepository.getStructure("3734", true));
		
	}

	@Test
	public void testUpdateActiveLocation() throws ParseException {
		PhysicalLocation physicalLocation = locationRepository.get("3734");
		assertEquals(0, physicalLocation.getProperties().getVersion());
		assertEquals(PropertyStatus.ACTIVE, physicalLocation.getProperties().getStatus());
		physicalLocation.getGeometry().setType(GeometryType.POLYGON);
		physicalLocation.getProperties().setStatus(PropertyStatus.ACTIVE);
		physicalLocation.getProperties().setGeographicLevel(3);

		Date effectiveStartDate = dateFormat.parse("2019-07-15");
		Date effectiveEndDate = dateFormat.parse("2020-07-15");
		physicalLocation.getProperties().setEffectiveStartDate(effectiveStartDate);
		physicalLocation.getProperties().setEffectiveEndDate(effectiveEndDate);
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);
		PhysicalLocation updatedLocation = locationRepository.get("3734");

		assertNotNull(updatedLocation);
		assertEquals(GeometryType.POLYGON, updatedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.ACTIVE, updatedLocation.getProperties().getStatus());
		assertEquals(3, updatedLocation.getProperties().getGeographicLevel());
		assertEquals(effectiveStartDate, updatedLocation.getProperties().getEffectiveStartDate());
		assertEquals(effectiveEndDate, updatedLocation.getProperties().getEffectiveEndDate());
		assertEquals(0, updatedLocation.getProperties().getVersion());

		assertNull(locationRepository.getStructure("3734", true));

	}
	
	@Test
	public void testUpdatePendingReviewLocation() throws ParseException {
		PhysicalLocation physicalLocation = locationRepository.get("3734");
		assertEquals(0, physicalLocation.getProperties().getVersion());
		assertEquals(PropertyStatus.ACTIVE, physicalLocation.getProperties().getStatus());
		physicalLocation.getGeometry().setType(GeometryType.POLYGON);
		physicalLocation.getProperties().setStatus(PropertyStatus.PENDING_REVIEW);
		physicalLocation.getProperties().setGeographicLevel(3);

		Date effectiveStartDate = dateFormat.parse("2019-07-15");
		Date effectiveEndDate = dateFormat.parse("2020-07-15");
		physicalLocation.getProperties().setEffectiveStartDate(effectiveStartDate);
		physicalLocation.getProperties().setEffectiveEndDate(effectiveEndDate);
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);
		PhysicalLocation updatedLocation = locationRepository.get("3734");

		assertNotNull(updatedLocation);
		assertEquals(GeometryType.POLYGON, updatedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.PENDING_REVIEW, updatedLocation.getProperties().getStatus());
		assertEquals(3, updatedLocation.getProperties().getGeographicLevel());
		assertEquals(effectiveStartDate, updatedLocation.getProperties().getEffectiveStartDate());
		assertEquals(effectiveEndDate, updatedLocation.getProperties().getEffectiveEndDate());
		assertEquals(0, updatedLocation.getProperties().getVersion());

		assertNull(locationRepository.getStructure("3734", true));

	}
	
	@Test
	public void testUpdateLocationWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationRepository.add(physicalLocation);
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testUpdateLocationNonExistingShouldNotChangeObject() {
		
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);
		
		assertNull(locationRepository.get("223232"));
		assertNull(locationRepository.getStructure("223232", true));
		
	}
	
	@Test
	public void testUpdateStructure() {
		PhysicalLocation structure = locationRepository.getStructure("90397", true);
		structure.getProperties().setCode("12121");
		structure.getProperties().setParentId("11");
		locationRepository.update(structure);
		
		PhysicalLocation updatedStructure = locationRepository.getStructure("90397", true);
		
		assertNotNull(updatedStructure);
		assertEquals("12121", updatedStructure.getProperties().getCode());
		assertEquals("11", updatedStructure.getProperties().getParentId());
		
		assertNull(locationRepository.get("90397"));
		
	}
	
	@Test
	public void testUpdateStructureWithoutId() {
		PhysicalLocation structure = new PhysicalLocation();
		locationRepository.add(structure);
		
		assertEquals(2, locationRepository.getAll().size());
		assertEquals(2, locationRepository.getAllStructures().size());
		
	}
	
	@Test
	public void testUpdateStructureExistingShouldNotChangeObject() {
		
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		locationRepository.update(physicalLocation);
		
		assertNull(locationRepository.get("223232"));
		assertNull(locationRepository.getStructure("223232", true));
		
	}
	
	@Test
	public void testGetAll() {
		List<PhysicalLocation> locations = locationRepository.getAll();
		assertEquals(2, locations.size());
		
		locationRepository.safeRemove(locationRepository.get("3734"));
		locationRepository.safeRemove(locationRepository.get("3735"));
		
		assertTrue(locationRepository.getAll().isEmpty());
		
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation location = createLocation(uuid);
		location.getProperties().setStatus(PropertyStatus.ACTIVE);
		locationRepository.add(location);
		
		locations = locationRepository.getAll();
		
		assertEquals(1, locations.size());
		assertEquals("223232", locations.get(0).getId());
		assertEquals(uuid, locations.get(0).getProperties().getUid());
		
	}
	
	@Test
	public void testSafeRemoveLocation() {
		
		assertNotNull(locationRepository.get("3734"));
		
		locationRepository.safeRemove(locationRepository.get("3734"));
		
		assertNull(locationRepository.get("3734"));
	}
	
	@Test
	public void testSafeRemoveStructure() {
		
		assertNotNull(locationRepository.getStructure("90397", true));
		
		locationRepository.safeRemove(locationRepository.getStructure("90397", true));
		
		assertNull(locationRepository.getStructure("90397", true));
	}
	
	@Test
	public void testSafeRemoveNonExistentLocation() {
		locationRepository.safeRemove(null);
		locationRepository.safeRemove(new PhysicalLocation());
		assertEquals(2, locationRepository.getAll().size());
		
		locationRepository.safeRemove(locationRepository.get("671198"));
		assertEquals(2, locationRepository.getAll().size());
		
	}
	
	@Test
	public void testFindLocationsByServerVersion() {
		
		List<PhysicalLocation> locations = locationRepository.findLocationsByServerVersion(1542378347106l);
		assertTrue(locations.isEmpty());
		
		locations = locationRepository.findLocationsByServerVersion(1l);
		System.out.println(ReflectionToStringBuilder.toString(locations.get(0)));
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertTrue(locations.get(0).getServerVersion() >= 1l);
		
		locations.get(0).setServerVersion(null);
		locationRepository.update(locations.get(0));
		
		locations = locationRepository.findLocationsByServerVersion(1l);
		assertTrue(locations.isEmpty());
		
	}
	
	@Test
	public void testFindStructuresByParentAndServerVersion() {
		
		List<PhysicalLocation> locations = locationRepository.findStructuresByParentAndServerVersion("3734", 1542376382859l);
		assertTrue(locations.isEmpty());
		
		locations = locationRepository.findStructuresByParentAndServerVersion("3734", 1542376382851l);
		assertEquals(1, locations.size());
		assertEquals("90397", locations.get(0).getId());
		assertEquals("3734", locations.get(0).getProperties().getParentId());
		assertEquals(1542376382851l, locations.get(0).getServerVersion().longValue());
		assertTrue(locations.get(0).getServerVersion() >= 1l);
		
		locations = locationRepository.findStructuresByParentAndServerVersion("3734,001", 1542376382851l);
		assertEquals(1, locations.size());
		assertEquals("90397", locations.get(0).getId());
		assertEquals("3734", locations.get(0).getProperties().getParentId());
		
		locations.get(0).setServerVersion(null);
		locationRepository.update(locations.get(0));
		
		locations = locationRepository.findStructuresByParentAndServerVersion("3734", 0l);
		assertTrue(locations.isEmpty());
	}
	
	@Test
	public void testFindByEmptyServerVersion() {
		
		List<PhysicalLocation> locations = locationRepository.findByEmptyServerVersion();
		assertTrue(locations.isEmpty());
		
		PhysicalLocation location = locationRepository.get("3734");
		location.setServerVersion(null);
		locationRepository.update(location);
		
		locations = locationRepository.findByEmptyServerVersion();
		
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		
	}
	
	@Test
	public void testFindStructuresByEmptyServerVersion() {
		
		List<PhysicalLocation> locations = locationRepository.findStructuresByEmptyServerVersion();
		assertTrue(locations.isEmpty());
		
		PhysicalLocation location = locationRepository.getStructure("90397", true);
		location.setServerVersion(null);
		locationRepository.update(location);
		
		locations = locationRepository.findStructuresByEmptyServerVersion();
		assertEquals(1, locations.size());
		assertEquals("90397", locations.get(0).getId());
		
	}
	
	private PhysicalLocation createLocation(String uuid) {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		physicalLocation.setJurisdiction(true);
		Geometry geometry = new Geometry();
		geometry.setType(GeometryType.MULTI_POLYGON);
		physicalLocation.setGeometry(geometry);
		LocationProperty properties = new LocationProperty();
		properties.setStatus(PropertyStatus.INACTIVE);
		properties.setUid(uuid);
		properties.setName("01_5");
		physicalLocation.setProperties(properties);
		return physicalLocation;
		
	}
	
	private PhysicalLocation createStructure(String uuid) {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("121212");
		physicalLocation.setType("Feature");
		Geometry geometry = new Geometry();
		geometry.setType(GeometryType.POLYGON);
		physicalLocation.setGeometry(geometry);
		LocationProperty properties = new LocationProperty();
		properties.setStatus(PropertyStatus.ACTIVE);
		properties.setUid(uuid);
		physicalLocation.setProperties(properties);
		return physicalLocation;
	}
	
	@Test
	public void testFindLocationsByNames() {
		
		List<PhysicalLocation> locations = locationRepository.findLocationsByNames("MKB_5", 0l);
		assertTrue(locations.isEmpty());
		
		PhysicalLocation parentLocation = gson.fromJson(parentJson, PhysicalLocation.class);
		parentLocation.setJurisdiction(true);
		locationRepository.add(parentLocation);
		
		locations = locationRepository.findLocationsByNames("MKB_5", 0l);
		assertEquals(1, locations.size());
		PhysicalLocation location = locations.get(0);
		assertEquals("MKB_5", location.getProperties().getName());
		assertEquals("Feature", location.getType());
		assertEquals(GeometryType.MULTI_POLYGON, location.getGeometry().getType());
		
		locations = locationRepository.findLocationsByNames("MKB_5,other_location_name", 0l);
		assertEquals(1, locations.size());
		location = locations.get(0);
		assertEquals("MKB_5", location.getProperties().getName());
		assertEquals("Feature", location.getType());
		assertEquals(GeometryType.MULTI_POLYGON, location.getGeometry().getType());
		
	}
	
	@Test
	public void testFindStructureAndFamilyDetailsWithFamilyDetails() throws SQLException {
		scripts.add("client.sql");
		populateDatabase();
		
		Client family = new Client(UUID.randomUUID().toString()).withFirstName("Otala").withLastName("Family");
		family.withAttribute(ClientsRepositoryImpl.RESIDENCE, "90397");
		clientsRepository.add(family);
		
		double latitude = -14.1619809;
		double longitude = 32.5978597;
		
		Collection<StructureDetails> details = locationRepository.findStructureAndFamilyDetails(latitude, longitude, 100);
		assertEquals(0, details.size());
		
		details = locationRepository.findStructureAndFamilyDetails(latitude, longitude, 1000);
		assertEquals(1, details.size());
		
		StructureDetails structureDetails = details.iterator().next();
		assertEquals("90397", structureDetails.getStructureId());
		assertEquals("3734", structureDetails.getStructureParentId());
		assertEquals("Residential Structure", structureDetails.getStructureType());
		assertEquals(family.getBaseEntityId(), structureDetails.getFamilyId());
		assertEquals(1, structureDetails.getFamilyMembers().size());
		assertEquals("d4eda055-60c6-44a4-ba48-61dfe6485bea", structureDetails.getFamilyMembers().iterator().next());
	}
	
	@Test
	public void testFindStructureAndFamilyDetailsWithoutFamilyDetails() {
		double latitude = -14.1619809;
		double longitude = 34.5978597;
		
		Collection<StructureDetails> details = locationRepository.findStructureAndFamilyDetails(latitude, longitude, 1000);
		assertEquals(0, details.size());
		
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation structure = createStructure(uuid);
		JsonArray cordinates = new JsonArray();
		cordinates.add(new JsonPrimitive(longitude));
		cordinates.add(new JsonPrimitive(latitude));
		structure.getGeometry().setCoordinates(cordinates);
		structure.getGeometry().setType(GeometryType.POINT);
		structure.getProperties().setParentId("2465476");
		structure.getProperties().setType("Larvacide Point");
		locationRepository.add(structure);
		
		details = locationRepository.findStructureAndFamilyDetails(latitude, longitude, 1000);
		assertEquals(1, details.size());
		
		StructureDetails structureDetails = details.iterator().next();
		assertEquals("121212", structureDetails.getStructureId());
		assertEquals("2465476", structureDetails.getStructureParentId());
		assertEquals("Larvacide Point", structureDetails.getStructureType());
		assertTrue(structureDetails.getFamilyMembers().isEmpty());
		
		assertTrue(locationRepository.findStructureAndFamilyDetails(latitude + 1, longitude, 1000).isEmpty());
		
		double metersPerDegree = 110637;
		assertEquals(1, locationRepository.findStructureAndFamilyDetails(latitude + 1, longitude, metersPerDegree).size());
	}
	
	@Test
	public void testFindLocationsByPropertiesWithoutProperties() {
		List<PhysicalLocation> locations = locationRepository.findLocationsByProperties(false, "21", null);
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertNull(locations.get(0).getGeometry());
		
		locations = locationRepository.findLocationsByProperties(true, "21", null);
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertNotNull(locations.get(0).getGeometry());
		
		locations = locationRepository.findLocationsByProperties(true, null, null);
		assertEquals(2, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertEquals(GeometryType.MULTI_POLYGON, locations.get(0).getGeometry().getType());
		assertEquals(267,
		    locations.get(0).getGeometry().getCoordinates().get(0).getAsJsonArray().get(0).getAsJsonArray().size());
		
		// test non-existent parent
		locations = locationRepository.findLocationsByProperties(true, "1233", null);
		assertTrue(locations.isEmpty());
		
	}
	
	@Test
	public void testFindLocationsByProperties() {
		
		Map<String, String> filters = new HashMap<>();
		filters.put("code", "3734");
		filters.put("name", "Bangladesh");
		filters.put("uid", "41587456-b7c8-4c4e-b433-23a786f742fc");
		List<PhysicalLocation> locations = locationRepository.findLocationsByProperties(true, null, filters);
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertEquals("Bangladesh", locations.get(0).getProperties().getName());
		assertEquals(GeometryType.MULTI_POLYGON, locations.get(0).getGeometry().getType());
		JsonArray coordinates = locations.get(0).getGeometry().getCoordinates().get(0).getAsJsonArray().get(0)
		        .getAsJsonArray();
		assertEquals(267, coordinates.size());
		
		JsonArray coordinate1 = coordinates.get(0).getAsJsonArray();
		assertEquals(32.59989007736522, coordinate1.get(0).getAsDouble(), 0);
		assertEquals(-14.167432040756012, coordinate1.get(1).getAsDouble(), 0);
		
		JsonArray coordinate67 = coordinates.get(66).getAsJsonArray();
		assertEquals(32.5988341383848, coordinate67.get(0).getAsDouble(), 0);
		assertEquals(-14.171814074659776, coordinate67.get(1).getAsDouble(), 0);
		
		// test non-existent property value
		filters.put("name", "name1");
		locations = locationRepository.findLocationsByProperties(true, null, filters);
		assertTrue(locations.isEmpty());
		
	}
	
	@Test
	public void testFindStructuresByPropertiesWithoutProperties() {
		List<PhysicalLocation> structures = locationRepository.findStructuresByProperties(false, "3734", null);
		assertEquals(1, structures.size());
		assertEquals("90397", structures.get(0).getId());
		assertNull(structures.get(0).getGeometry());
		
		structures = locationRepository.findStructuresByProperties(true, "3734", null);
		assertEquals(1, structures.size());
		assertEquals("90397", structures.get(0).getId());
		assertNotNull(structures.get(0).getGeometry());
		
		structures = locationRepository.findStructuresByProperties(true, null, null);
		assertEquals(2, structures.size());
		assertEquals("90397", structures.get(0).getId());
		assertEquals(GeometryType.POLYGON, structures.get(0).getGeometry().getType());
		assertEquals(2,
		    structures.get(0).getGeometry().getCoordinates().get(0).getAsJsonArray().get(0).getAsJsonArray().size());
		
		// test non-existent parent
		structures = locationRepository.findLocationsByProperties(true, "1233", null);
		assertTrue(structures.isEmpty());
	}
	
	@Test
	public void testFindStructuresByProperties() {
		
		Map<String, String> filters = new HashMap<>();
		filters.put("code", "21384443");
		filters.put("geographicLevel", "5");
		filters.put("type", "Residential Structure");
		List<PhysicalLocation> locations = locationRepository.findStructuresByProperties(true, null, filters);
		assertEquals(1, locations.size());
		assertEquals("90397", locations.get(0).getId());
		assertEquals("21384443", locations.get(0).getProperties().getCode());
		assertNull(locations.get(0).getProperties().getName());
		assertEquals("Residential Structure", locations.get(0).getProperties().getType());
		assertEquals(GeometryType.POLYGON, locations.get(0).getGeometry().getType());
		assertEquals(2,
		    locations.get(0).getGeometry().getCoordinates().get(0).getAsJsonArray().get(0).getAsJsonArray().size());
		
		// test non-existent property
		filters.put("name", "House23");
		locations = locationRepository.findStructuresByProperties(true, null, filters);
		assertTrue(locations.isEmpty());
		
	}
	
	@Test
	public void testFindLocationByIdWithChildren() {
		
		List<PhysicalLocation> locations = locationRepository.findLocationByIdWithChildren(true, "3734", 10);
		assertEquals(2, locations.size());
		for (PhysicalLocation location : locations) {
			MatcherAssert.assertThat(location.getId(), either(is("3734")).or(is("3735")));
		}
	}
	
	
	@Test
	public void testFindLocationByIdsWithChildren() {
		
		List<PhysicalLocation> locations = locationRepository.findLocationByIdsWithChildren(true, new HashSet<>(Arrays.asList("3734","3735")), 10);
		assertEquals(2, locations.size());
		for (PhysicalLocation location : locations) {
			MatcherAssert.assertThat(location.getId(), either(is("3734")).or(is("3735")));
		}
	}
	
	@Test
	public void testFindAllLocationIdsShouldOrderByServerVersion() {
		
		Pair<List<String>, Long> idsModel = locationRepository.findAllLocationIds(-2l, 10);
		List<String> locationsIds = idsModel.getLeft();
		assertEquals(2, locationsIds.size());
		assertEquals("3735", locationsIds.get(0));
		assertEquals("3734", locationsIds.get(1));
		assertEquals(1542378347104l, idsModel.getRight().longValue());
		
	}
	
	@Test
	public void testFindAllLocationIdsShouldLimitByGivenParam() {
		
		Pair<List<String>, Long> idsModel = locationRepository.findAllLocationIds(-2l, 1);
		List<String> locationsIds = idsModel.getLeft();
		assertEquals(1, locationsIds.size());
		assertEquals("3735", locationsIds.get(0));
		assertEquals(-1l, idsModel.getRight().longValue());
		
	}
	
	@Test
	public void testFindAllStructureIdsShouldOrderByServerVersion() {
		
		Pair<List<String>, Long> idsModel = locationRepository.findAllStructureIds(0l, 10);
		List<String> structureIds = idsModel.getLeft();
		assertEquals(2, structureIds.size());
		assertEquals("90397", structureIds.get(0));
		assertEquals("90398", structureIds.get(1));
		assertEquals(1542376382862l, idsModel.getRight().longValue());
	}
	
	@Test
	public void testFindAllStructureIdsShouldLimitByGivenParam() {
		
		Pair<List<String>, Long> idsModel = locationRepository.findAllStructureIds(0l, 1);
		List<String> structureIds = idsModel.getLeft();
		assertEquals(1, structureIds.size());
		assertEquals("90397", structureIds.get(0));
		assertEquals(1542376382851l, idsModel.getRight().longValue());
	}
	
	@Test
	public void testSearchlLocationsWithFilters() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(2l);
		locationTagMap.setLocationTagId(2l);
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("a");
		locationSearchBean.setLocationTagId(2l);
		locationSearchBean.setPageSize(20);
		locationSearchBean.setPageNumber(1);
		locationSearchBean.setOrderByFieldName("id");
		locationSearchBean.setOrderByType(OrderByType.ASC);
		locationTagRepository.addLocationTagMap(locationTagMap);
		List<PhysicalLocation> locations = new ArrayList<PhysicalLocation>();
		locations = locationRepository.searchLocations(locationSearchBean);
		assertNotNull(locations);
		assertEquals(1l, locations.size());
	}
	
	@Test
	public void testSearchLocationsWithoutFilters() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(2l);
		locationTagMap.setLocationTagId(2l);
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationTagRepository.addLocationTagMap(locationTagMap);
		List<PhysicalLocation> locations = new ArrayList<PhysicalLocation>();
		locations = locationRepository.searchLocations(locationSearchBean);
		assertNotNull(locations);
		assertEquals(1l, locations.size());
		locationSearchBean.setPageSize(0);
		locations = locationRepository.searchLocations(locationSearchBean);
		assertEquals(1l, locations.size());
	}
	
	@Test
	public void testSearchLocationsWithEmptyRecords() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(2l);
		locationTagMap.setLocationTagId(2l);
		
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("no location");
		locationTagRepository.addLocationTagMap(locationTagMap);
		List<PhysicalLocation> locations = new ArrayList<PhysicalLocation>();
		locations = locationRepository.searchLocations(locationSearchBean);
		assertTrue(locations.isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchLocationsWithoutPageNumber() {
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("a");
		locationSearchBean.setLocationTagId(2l);
		locationSearchBean.setPageSize(20);
		locationSearchBean.setPageNumber(0);
		locationRepository.searchLocations(locationSearchBean);
	}
	
	@Test
	public void testCountSearchLocationsWithFilters() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(2l);
		locationTagMap.setLocationTagId(2l);
		locationTagRepository.addLocationTagMap(locationTagMap);
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("a");
		locationSearchBean.setLocationTagId(2l);
		int totalCount = locationRepository.countSearchLocations(locationSearchBean);
		assertEquals(1l, totalCount);
	}
	
	@Test
	public void testCountSearchLocationsWithoutFilters() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(2l);
		locationTagMap.setLocationTagId(2l);
		locationTagRepository.addLocationTagMap(locationTagMap);
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("Khulna");
		locationSearchBean.setLocationTagId(2l);
		int totalCount = locationRepository.countSearchLocations(locationSearchBean);
		assertEquals("total count should be zero", 0, totalCount);
	}
	
	@Test
	public void testfindParentLocationsInclusive() {
		
		PhysicalLocation location = locationRepository.get("3734");
		List<LocationTag> expectedTags = locationTagRepository.getAll();
		location.setLocationTags(new HashSet<>(expectedTags));
		locationRepository.update(location);
		
		Set<String> identifiers = Collections.singleton("3735");
		Set<LocationDetail> locations = locationRepository.findParentLocationsInclusive(identifiers);
		assertEquals(2, locations.size());
		for (LocationDetail l : locations) {
			MatcherAssert.assertThat(l.getIdentifier(), either(is("3734")).or(is("3735")));
		}
		
		assertEquals(2, locationRepository.findParentLocationsInclusive(new HashSet<>(Arrays.asList("3735", "21"))).size());
		
		//Location without a parent
		locations = locationRepository.findParentLocationsInclusive(Collections.singleton("3734"));
		assertEquals(1, locations.size());
		LocationDetail actualLocationDetail = locations.iterator().next();
		assertEquals("3734", actualLocationDetail.getIdentifier());
		assertEquals("Bangladesh", actualLocationDetail.getName());
		assertEquals("21", actualLocationDetail.getParentId());
		assertEquals(1l, actualLocationDetail.getId().longValue());
		List<String> tags = Arrays.asList(actualLocationDetail.getTags().split(","));
		assertEquals(expectedTags.size(), tags.size());
		assertTrue(tags.contains(expectedTags.get(0).getName()));
		assertTrue(tags.contains(expectedTags.get(1).getName()));
		
		//Non existent location
		assertEquals(0, locationRepository.findParentLocationsInclusive(Collections.singleton("21")).size());
	}

	@Test
	public void testfindParentLocationsInclusiveWithReturnTagsFalse() {

		PhysicalLocation location = locationRepository.get("3734");
		List<LocationTag> expectedTags = locationTagRepository.getAll();
		location.setLocationTags(new HashSet<>(expectedTags));
		locationRepository.update(location);

		Set<String> identifiers = Collections.singleton("3735");
		Set<LocationDetail> locations = locationRepository.findParentLocationsInclusive(identifiers, false);
		assertEquals(2, locations.size());
		for (LocationDetail l : locations) {
			MatcherAssert.assertThat(l.getIdentifier(), either(is("3734")).or(is("3735")));
		}

		assertEquals(2, locationRepository.findParentLocationsInclusive(new HashSet<>(Arrays.asList("3735", "21"))).size());

		//Location without a parent
		locations = locationRepository.findParentLocationsInclusive(Collections.singleton("3734"), false);
		assertEquals(1, locations.size());
		LocationDetail actualLocationDetail = locations.iterator().next();
		assertEquals("3734", actualLocationDetail.getIdentifier());
		assertEquals("Bangladesh", actualLocationDetail.getName());
		assertEquals("21", actualLocationDetail.getParentId());
		assertEquals(1l, actualLocationDetail.getId().longValue());
		assertNull(actualLocationDetail.getTags());

		//Non existent location
		assertEquals(0, locationRepository.findParentLocationsInclusive(Collections.singleton("21")).size());
	}

	@Test
	public void testCountStructuresByParentAndServerVersion() {

		Long locations = locationRepository.countStructuresByParentAndServerVersion("3734", 1542376382859l);
		assertEquals(0, locations.longValue());

		locations = locationRepository.countStructuresByParentAndServerVersion("3734", 1542376382851l);
		assertEquals(1, locations.longValue());

		locations = locationRepository.countStructuresByParentAndServerVersion("3734,001", 1542376382851l);
		assertEquals(1, locations.longValue());

	}

	@Test
	public void testcountLocationsByServerVersion() {

		Long locations = locationRepository.countLocationsByServerVersion(1542378347106l);
		assertEquals(0, locations.longValue());

		locations = locationRepository.countLocationsByServerVersion(1l);
		assertEquals(1, locations.longValue());

	}

	@Test
	public void testCountLocationsByNames() {

		Long locations = locationRepository.countLocationsByNames("MKB_5", 0l);
		assertEquals(0, locations.longValue());

		PhysicalLocation parentLocation = gson.fromJson(parentJson, PhysicalLocation.class);
		parentLocation.setJurisdiction(true);
		locationRepository.add(parentLocation);

		locations = locationRepository.countLocationsByNames("MKB_5", 0l);
		assertEquals(1, locations.longValue());

		locations = locationRepository.countLocationsByNames("MKB_5,other_location_name", 0l);
		assertEquals(1, locations.longValue());

	}

	@Test
	public void testSelectDetailsByPlanId() {

		String planIdentifier = "a8b3010c-1ba5-556d-8b16-71266397b8b9";
		Set<LocationDetail> locationDetails = locationRepository.findLocationDetailsByPlanId(planIdentifier);
		assertFalse(locationDetails.isEmpty());
		assertEquals(1, locationDetails.size());
	}

	@Test
	public void testFindLocationWithDescendants() {
		Set<LocationDetail> locations = locationRepository.findLocationWithDescendants("3734", false);

		assertEquals(2, locations.size());

		for (LocationDetail location : locations) {
			MatcherAssert.assertThat(location.getIdentifier(), either(is("3734")).or(is("3735")));
		}

		locations = locationRepository.findLocationWithDescendants("3735", true);
		assertEquals(1, locations.size());
		LocationDetail actualLocationdetail = locations.iterator().next();
		assertEquals("3735", actualLocationdetail.getIdentifier());
		assertEquals("Dhaka", actualLocationdetail.getName());
		assertEquals("3734", actualLocationdetail.getParentId());
		assertEquals(2l, actualLocationdetail.getId().longValue());

		assertEquals(0, locationRepository.findLocationWithDescendants("21", false).size());
	}

	@Test
	public void testFindStructureCountsForLocation() {
		Set<String> locationIds = new HashSet<>();
		locationIds.add("3724");
		locationIds.add("3734");

		List<StructureCount> structureCounts = locationRepository.findStructureCountsForLocation(locationIds);
		structureCounts.size();
		assertEquals("3724", structureCounts.get(0).getParentId());
		assertEquals(1, structureCounts.get(0).getCount());

		assertEquals("3734", structureCounts.get(1).getParentId());
		assertEquals(1, structureCounts.get(0).getCount());

	}

	@Test
	public void testFindChildLocationByJurisdiction() {

		List<String> locationIds = locationRepository.findChildLocationByJurisdiction("3734");
		assertEquals(2, locationIds.size());
		for (String id : locationIds) {
			MatcherAssert.assertThat(id, either(is("3734")).or(is("3735")));
		}
	}

	@Test
	public void testFindLocationByIdentifierAndStatus() {

		PhysicalLocation actualLocation = locationRepository.findLocationByIdentifierAndStatus("3734", Collections.singletonList(LocationProperty.PropertyStatus.ACTIVE.name()), false);

		assertEquals("3734", actualLocation.getId());
		assertEquals("Bangladesh", actualLocation.getProperties().getName());
		assertEquals("21", actualLocation.getProperties().getParentId());

		PhysicalLocation actualInactiveLocation = locationRepository.findLocationByIdentifierAndStatus("3734", Collections.singletonList(LocationProperty.PropertyStatus.INACTIVE.name()), false);
		assertNull(actualInactiveLocation);

	}

}
