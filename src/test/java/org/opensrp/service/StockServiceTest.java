package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.opensrp.repository.StocksRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.search.StockSearchBean;
import org.opensrp.validator.InventoryDataValidator;
import org.smartregister.domain.*;
import org.smartregister.utils.PropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.constants.InventoryConstants.*;

/**
 * Integration Tests for {@link StockService}.
 */
public class StockServiceTest extends BaseRepositoryTest {

    public static Gson gson = new GsonBuilder().registerTypeAdapter(LocationProperty.class, new PropertiesConverter())
            .setDateFormat("yyyy-MM-dd'T'HHmm").create();
    public static String parentJson = "{\"type\":\"Feature\",\"id\":\"3734\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[32.59989007736522,-14.167432040756012],[32.599899215376524,-14.167521429770147],[32.59990104639621,-14.167611244163538],[32.599895558733124,-14.16770091736339],[32.59989008366051,-14.167739053114758],[32.59993602462787,-14.16775581827636],[32.600019423257834,-14.167793873088726],[32.60009945932262,-14.167838272618155],[32.60017562650404,-14.167888735376776],[32.60024744456398,-14.16794494480331],[32.60031445844555,-14.168006542967532],[32.60037624546743,-14.168073141362354],[32.60043241352514,-14.168144319105009],[32.6004826082858,-14.168219626534494],[32.60052651318807,-14.168298587010156],[32.60056384854283,-14.168380700508862],[32.60059438052627,-14.168465449920518],[32.600617914884936,-14.168552297450503],[32.60062693688366,-14.16860096246438],[32.600629671722004,-14.168608553641775],[32.600653206080665,-14.16869540117176],[32.60066959442628,-14.168783799132996],[32.600678733336906,-14.168873188147074],[32.60068056435659,-14.168963002540465],[32.600675075794186,-14.169052674840998],[32.60066230362247,-14.169141638475764],[32.60064232698187,-14.169229331368513],[32.60061527357607,-14.169315197738115],[32.60058131337695,-14.169398695293522],[32.6005406613225,-14.169479297031899],[32.60049357461884,-14.169556492138383],[32.600440351840795,-14.169629793180375],[32.60038132753629,-14.169698736107762],[32.60031687672256,-14.169762885648595],[32.60024740499381,-14.169821836208712],[32.60017335391722,-14.169875214569458],[32.600095189341566,-14.169922684384344],[32.600073628095515,-14.169933562583822],[32.60007671007219,-14.169963706060116],[32.600078541091875,-14.170053520453562],[32.60007305252941,-14.170143193653415],[32.600060280357695,-14.170232157288183],[32.600040303717094,-14.170319849281555],[32.60001324941197,-14.170405715651212],[32.599979289212854,-14.170489214105885],[32.59993897080693,-14.170569153943234],[32.599943946755786,-14.170617831547645],[32.599945777775474,-14.170707646840356],[32.59994028921301,-14.170797319140943],[32.59992751704135,-14.170886282775712],[32.59990754040069,-14.170973974769083],[32.59988048609562,-14.171059841138685],[32.5998465258965,-14.171143339593414],[32.59980587384205,-14.17122394043247],[32.59975878713834,-14.171301135538954],[32.59970556346104,-14.171374436580948],[32.59964653915648,-14.171443379508332],[32.59958208744342,-14.171507529049165],[32.59951261571467,-14.171566479609282],[32.59943856373877,-14.171619858869349],[32.59936039916312,-14.171667327784915],[32.599278615715605,-14.17170858778104],[32.59919373140565,-14.17174337715511],[32.599106283128485,-14.171771477371749],[32.59901682216866,-14.171792708566613],[32.59892591599828,-14.171806937640042],[32.5988341383848,-14.171814074659776],[32.59874206939105,-14.171814074659776],[32.598650291777574,-14.171806937640042],[32.59855938470787,-14.171792708566613],[32.59846992464736,-14.171771477371749],[32.59838247547094,-14.17174337715511],[32.5983273353383,-14.17172077809141],[32.59831050992216,-14.17173505572822],[32.59823645704688,-14.17178843408891],[32.59815829247128,-14.171835903903796],[32.59807650992303,-14.171877163899921],[32.597991625613076,-14.17191195327399],[32.59790417643665,-14.17194005259131],[32.597814715476765,-14.171961283786173],[32.59772380930639,-14.171975513758923],[32.59763203169297,-14.171982650778657],[32.59753996269916,-14.171982650778657],[32.59744818508574,-14.171975513758923],[32.597357278016034,-14.171961283786173],[32.59726781795547,-14.17194005259131],[32.59718036877905,-14.17191195327399],[32.597125046084045,-14.171889279566528],[32.59711651511515,-14.171899244054828],[32.59705206340203,-14.171963393595718],[32.59698259167334,-14.172022344155835],[32.59690853879806,-14.172075722516524],[32.59683037422246,-14.172123192331412],[32.596748590774894,-14.172164452327593],[32.596669124880236,-14.172197021275451],[32.59666535402294,-14.172199310949395],[32.596583571474696,-14.172240570945519],[32.59649868716474,-14.172275360319588],[32.59641123798832,-14.172303459636908],[32.596321777028436,-14.172324691731092],[32.59623087085805,-14.172338920804519],[32.59613909234531,-14.172346057824257],[32.5960470233515,-14.172346057824257],[32.595955245738025,-14.172338920804519],[32.59586433866832,-14.172324691731092],[32.595803283694636,-14.172310200954938],[32.59573533092083,-14.172367862786585],[32.59566127804561,-14.172421241147333],[32.59558311346996,-14.172468710062901],[32.59551500691208,-14.172503069560946],[32.59545514174147,-14.172546221730727],[32.59537697626649,-14.172593691545558],[32.59529519371824,-14.172634951541738],[32.59521030850897,-14.17266974091575],[32.59512285933255,-14.17269784023307],[32.59503339927198,-14.172719072327254],[32.59494249220228,-14.172733301400683],[32.59485071368954,-14.172740438420476],[32.59475864469573,-14.172740438420476],[32.59466686618299,-14.172733301400683],[32.594575960012605,-14.172719072327254],[32.59448649905272,-14.17269784023307],[32.59445585105675,-14.172687992656677],[32.59442677507565,-14.172690253552332],[32.59433470518257,-14.172690253552332],[32.5942429275691,-14.17268311653254],[32.59415202049939,-14.172668887459167],[32.594062559539566,-14.172647656264303],[32.59397511036309,-14.172619556946927],[32.593890226053134,-14.172584766673594],[32.59380844260562,-14.17254350667747],[32.593730278029966,-14.172496037761903],[32.593656225154746,-14.172442658501836],[32.593586753425996,-14.17238370794172],[32.593567544806376,-14.172364589254355],[32.59351203505241,-14.172317486362884],[32.593447582440035,-14.172253336821994],[32.59338855813547,-14.17218439389461],[32.593335334458175,-14.172111092852617],[32.59328824685514,-14.172033897746132],[32.59324759480069,-14.171953296907077],[32.593213634601625,-14.171869799351725],[32.5931865802965,-14.171783932082747],[32.5931666036559,-14.171696240089375],[32.59315383148419,-14.171607276454608],[32.59314834292172,-14.171517604154076],[32.5931501739414,-14.171427788861308],[32.59315931285203,-14.17133840074655],[32.59317570119771,-14.17125000278537],[32.59319923555631,-14.171163155255385],[32.59322976753981,-14.17107840584373],[32.593267104693155,-14.170996292344967],[32.59331100869616,-14.170917331869305],[32.59336120435614,-14.170842024439821],[32.59341737331317,-14.170770846697167],[32.59347916123437,-14.170704248302341],[32.59354617601525,-14.17064265013812],[32.593617994075196,-14.170586440711643],[32.593694163055204,-14.170535977952964],[32.59377420001937,-14.170491578423535],[32.593857599548635,-14.170453523611172],[32.59394383374098,-14.170422054534129],[32.594032357607254,-14.170397369942634],[32.594122611769194,-14.170379625419342],[32.594214026056534,-14.170368934278827],[32.5943060213059,-14.170365362171708],[32.59439801655525,-14.170368934278827],[32.594402085987554,-14.170369410020214],[32.59439156302028,-14.170336010998085],[32.594371587279,-14.170248319004656],[32.594358814207965,-14.17015935536989],[32.59435618369099,-14.170116383963887],[32.594347703084054,-14.170089466355762],[32.59432772734277,-14.170001773463014],[32.594314954271795,-14.169912809828245],[32.59430946660865,-14.169823137527715],[32.59431129672901,-14.169733323134324],[32.594320435639645,-14.169643934120245],[32.594336823985316,-14.16955553615901],[32.59436035834392,-14.169468688629022],[32.59439089032736,-14.169383940116688],[32.59442822658144,-14.16930182571866],[32.59447213148371,-14.169222865243],[32.59452232624443,-14.169147557813517],[32.59457849520146,-14.169076380070862],[32.59464028132396,-14.169009781676039],[32.5947072961049,-14.168948183511816],[32.59477911416485,-14.168891974984604],[32.59485528224553,-14.168841511326661],[32.59493531831032,-14.168797111797232],[32.595018716940274,-14.168759056984868],[32.59510495113261,-14.168727587907824],[32.595193474099574,-14.168702903316273],[32.5952837282615,-14.168685158792982],[32.59537514075021,-14.168674467652522],[32.59545842696491,-14.168671234589736],[32.5954623282239,-14.168656837343121],[32.5954928602074,-14.168572088830786],[32.595530195562105,-14.168489974432703],[32.59557410046443,-14.168411013957098],[32.595624295225086,-14.168335707426932],[32.595680463282804,-14.168264529684278],[32.59574225030468,-14.168197930390136],[32.595809264186244,-14.168136332225913],[32.59588108224619,-14.1680801236987],[32.59595725032687,-14.168029660040759],[32.596033266422175,-14.16798749083],[32.59606430112666,-14.167931676205853],[32.59611449588732,-14.16785636877637],[32.59617066394503,-14.167785191033715],[32.59623245006759,-14.167718592638892],[32.59629946394915,-14.167656993575347],[32.596371282009095,-14.167600785048137],[32.596447450089784,-14.167550322289514],[32.59652748525531,-14.167505922760085],[32.59661088388526,-14.167467867947721],[32.59669711717828,-14.167436398870677],[32.59678564014524,-14.167411714279126],[32.59687589340779,-14.167393969755835],[32.5969673058965,-14.167383278615375],[32.59705930024654,-14.1673797065082],[32.597151294596635,-14.167383278615375],[32.59724270708534,-14.167393969755835],[32.5973329603479,-14.167411714279126],[32.59742148331486,-14.167436398870677],[32.59746305357709,-14.16745156953425],[32.59747073828402,-14.167423210312847],[32.59750126936814,-14.167338461800512],[32.597508451353974,-14.167322665208815],[32.59752348801862,-14.167280928571927],[32.59752577679319,-14.16727248213931],[32.59755630787737,-14.167187732727655],[32.59759364413145,-14.16710561832957],[32.5976375481344,-14.16702665875323],[32.59768774289506,-14.166951351323744],[32.59774391095277,-14.16688017358109],[32.59780569707533,-14.166813574287005],[32.59787271005757,-14.166751976122782],[32.59794452811752,-14.16669576759557],[32.598020695298885,-14.166645303937571],[32.59810073136373,-14.166600904408142],[32.59818412909431,-14.166562850495096],[32.598270361488055,-14.166531381418054],[32.598358884455024,-14.166506696826557],[32.59844913681826,-14.166488952303268],[32.59852580672065,-14.166479984263844],[32.598559981857704,-14.166473265428806],[32.598651394346405,-14.166462574288346],[32.598743387797185,-14.166459002181169],[32.59883538214722,-14.166462574288346],[32.59892679373661,-14.166473265428806],[32.599017046999165,-14.166491009952097],[32.59910556996613,-14.166515694543648],[32.599191802359826,-14.166547163620635],[32.59927520009046,-14.166585218433056],[32.599355236155304,-14.166629617962426],[32.599431403336666,-14.166680081620425],[32.5995032204973,-14.16673629014764],[32.59957023437886,-14.166797888311862],[32.59963202050142,-14.16686448670663],[32.59968818855913,-14.166935664449284],[32.59973838331979,-14.167010971878824],[32.59978228732274,-14.167089932354429],[32.5998196226775,-14.167172046752512],[32.59985015466094,-14.167256795264848],[32.5998736890196,-14.167343642794833],[32.59989007736522,-14.167432040756012]]]]},\"properties\":{\"uid\":\"41587456-b7c8-4c4e-b433-23a786f742fc\",\"code\":\"3734\",\"type\":\"Intervention Unit\",\"status\":\"Active\",\"parentId\":\"21\",\"name\":\"01_5\",\"geographicLevel\":4,\"effectiveStartDate\":\"2015-01-01T0000\",\"version\":0}}";
    @InjectMocks
    private StockService stockService;
    @Autowired
    @Qualifier("stocksRepositoryPostgres")
    private StocksRepository stocksRepository;
    @Mock
    private ProductCatalogueService productCatalogueService;
    @Mock
    private PhysicalLocationService physicalLocationService;
    @Mock
    private InventoryDataValidator inventoryDataValidator;

    @Before
    public void setup() {
        initMocks(this);

    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        scripts.add("stock.sql");
        scripts.add("product_catalogue.sql");
        return scripts;
    }

    @Override
    public void populateDatabase() throws SQLException {
        super.populateDatabase();
        stockService = new StockService(stocksRepository, productCatalogueService, physicalLocationService, inventoryDataValidator);
        Stock stock1 = new Stock("123", "VT", "TT", "4-2", 10, Long.parseLong("20062017"), "TF",
                Long.parseLong("20062017"), Long.parseLong("12345"));
        Stock stock2 = new Stock("123", "VT", "TT", "4-2", 10, Long.parseLong("20062017"), "TF",
                Long.parseLong("20062017"), Long.parseLong("12345"));
        stockService.addStock(stock1);
        stockService.addStock(stock2);
    }

    @Test
    public void shouldSaveStock() {
        Stock stock = new Stock("124", "VT1", "TT1", "4-2", 10, Long.parseLong("20062017"), "	TF",
                Long.parseLong("20062017"), Long.parseLong("12345"));

        Stock savedStock = stockService.addorUpdateStock(stock);
        Assert.assertNotNull(savedStock.getServerVersion());
        Assert.assertEquals(savedStock.getProviderid(), stock.getProviderid());

    }

    @Test
    public void shouldUpdateStockWithSaveorUpdate() {
        List<Stock> stocks = stockService.findAllByProviderid("4-2");
        for (Stock stock : stocks) {
            stock.setVaccineTypeId("12334");
            stock.setTransactionType("Restocking_1");
            stock.setValue(stock.getValue() * 2);
            stockService.addorUpdateStock(stock);
        }
        stocks = stockService.findAllByProviderid("4-2");
        for (Stock savedStock : stocks) {
            Assert.assertEquals(0, Minutes.minutesBetween(savedStock.getDateEdited(), DateTime.now()).getMinutes());
            Assert.assertEquals("12334", savedStock.getVaccineTypeId());
            Assert.assertEquals("Restocking_1", savedStock.getTransactionType());
            Assert.assertEquals(20, savedStock.getValue());
        }
    }

    @Test
    public void shouldUpdateStock() {
        List<Stock> stocks = stockService.findAllByProviderid("4-2");
        for (Stock stock : stocks) {
            stock.setVaccineTypeId("12334");
            stock.setTransactionType("Restocking_1");
            stock.setValue(stock.getValue() * 2);
            stockService.updateStock(stock);
        }
        stocks = stockService.findAllByProviderid("4-2");
        for (Stock savedStock : stocks) {
            Assert.assertEquals(0, Minutes.minutesBetween(savedStock.getDateEdited(), DateTime.now()).getMinutes());
            Assert.assertEquals("12334", savedStock.getVaccineTypeId());
            Assert.assertEquals("Restocking_1", savedStock.getTransactionType());
            Assert.assertEquals(20, savedStock.getValue());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateStockForNewStock() {
        Stock stock = new Stock("124", "VT1", "TT1", "4-2", 10, Long.parseLong("20062017"), "	TF",
                Long.parseLong("20062017"), Long.parseLong("12345"));
        stockService.updateStock(stock);
    }

    @Test
    public void shouldFetchStocksByProcider() throws Exception {
        List<Stock> fecthedListByProviderid = stockService.findAllByProviderid("4-2");
        for (Stock stock : fecthedListByProviderid)
            Assert.assertEquals("4-2", stock.getProviderid());
    }

    @Test
    public void shouldFetchAll() throws Exception {
        List<Stock> fecthedListAll = stockService.getAll();
        Assert.assertEquals(17, fecthedListAll.size());
    }

    @Test
    public void testAddInventory() {
        Inventory inventory = createInventory();
        List<String> donorsList = createDonorList();
        List<String> sectionsList = createSectionsList();
        when(productCatalogueService.getProductCatalogueByName(anyString())).thenReturn(createProductCatalogue());
        when(inventoryDataValidator.getValidDonors()).thenReturn(donorsList);
        when(inventoryDataValidator.getValidUnicefSections()).thenReturn(sectionsList);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(createLocation());
        stockService.addInventory(inventory, "John");
        StockSearchBean stockSearchBean = new StockSearchBean();
        List<String> locations = new ArrayList<>();
        locations.add("loc-1");
        stockSearchBean.setLocations(locations);
        stockSearchBean.setPageNumber(1);
        List<Stock> stockList = stockService.getStocksByServicePointId(stockSearchBean);
        Assert.assertEquals(1, stockList.size());
    }

    @Test
    public void testAddInventoryWithProviderId() {
        Inventory inventory = createInventory();
        inventory.setProviderId("test provider");
        List<String> donorsList = createDonorList();
        List<String> sectionsList = createSectionsList();
        when(productCatalogueService.getProductCatalogueByName(anyString())).thenReturn(createProductCatalogue());
        when(inventoryDataValidator.getValidDonors()).thenReturn(donorsList);
        when(inventoryDataValidator.getValidUnicefSections()).thenReturn(sectionsList);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(createLocation());
        stockService.addInventory(inventory, "John");
        StockSearchBean stockSearchBean = new StockSearchBean();
        List<String> locations = new ArrayList<>();
        locations.add("loc-1");
        stockSearchBean.setLocations(locations);
        stockSearchBean.setPageNumber(1);
        List<Stock> stockList = stockService.getStocksByServicePointId(stockSearchBean);
        Assert.assertEquals(1, stockList.size());
        Assert.assertEquals("test provider", stockList.get(0).getProviderid());
    }

    @Test
    public void testAddInventoryWithQuantityAsNull() {
        Inventory inventory = createInventory();
        inventory.setQuantity(null);
        List<String> donorsList = createDonorList();
        List<String> sectionsList = createSectionsList();
        when(productCatalogueService.getProductCatalogueByName(anyString())).thenReturn(createProductCatalogue());
        when(inventoryDataValidator.getValidDonors()).thenReturn(donorsList);
        when(inventoryDataValidator.getValidUnicefSections()).thenReturn(sectionsList);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(createLocation());
        stockService.addInventory(inventory, "John");
        StockSearchBean stockSearchBean = new StockSearchBean();
        List<String> locations = new ArrayList<>();
        locations.add("loc-1");
        stockSearchBean.setLocations(locations);
        stockSearchBean.setPageNumber(1);
        List<Stock> stockList = stockService.getStocksByServicePointId(stockSearchBean);
        Assert.assertEquals(1, stockList.size());
        Assert.assertEquals(1, stockList.get(0).getValue());
    }

    @Test
    public void testUpdate() {
        Inventory inventory = createInventory();
        List<String> donorsList = createDonorList();
        List<String> sectionsList = createSectionsList();
        when(inventoryDataValidator.getValidDonors()).thenReturn(donorsList);
        when(inventoryDataValidator.getValidUnicefSections()).thenReturn(sectionsList);
        when(productCatalogueService.getProductCatalogueByName(anyString())).thenReturn(createProductCatalogue());
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(createLocation());
        stockService.addInventory(inventory, "John");
        inventory.setDonor("BMGF");
        Stock stock = stockService.findByIdentifierAndServicePointId("1", "loc-1");
        inventory.setStockId(stock.getId());
        stockService.updateInventory(inventory, "John");
        Stock updatedStock = stockService.findByIdentifierAndServicePointId("1", "loc-1");
        Assert.assertEquals("BMGF", updatedStock.getDonor());
    }

    @Test
    public void testValidateBulkInventoryDataWithValidationErrors() throws ParseException {
        List<Map<String, String>> csvStocks = new ArrayList<>();
        Map<String, String> csvRow = new HashMap<>();
        csvRow.put(SERVICE_POINT_ID, "89879388");
        csvRow.put(PRODUCT_NAME, "Midwifery Kit");
        csvRow.put(PRODUCT_ID, "990222");
        csvRow.put(QUANTITY, "-1");
        csvRow.put(DELIVERY_DATE, "04/08/2020");
        csvRow.put(UNICEF_SECTION, "WASH");
        csvRow.put(DONOR, "Gates");
        csvRow.put(SERIAL_NUMBER, "12345");
        csvRow.put(PO_NUMBER, "897");
        csvStocks.add(csvRow);

        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(PRODUCT_CATALOG_DOES_NOT_EXISTS);

        when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(createProductCatalogue());
        when(physicalLocationService.getLocation(anyString(), anyBoolean(), anyBoolean())).thenReturn(createLocation());
        when(inventoryDataValidator.getValidationErrors(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(validationErrors);
        CsvBulkImportDataSummary csvBulkImportDataSummary = stockService.validateBulkInventoryData(csvStocks);
        Assert.assertEquals(1, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
    }

    @Test
    public void testConvertandPersistInventorydata() throws ParseException {
        List<Map<String, String>> csvStocks = new ArrayList<>();
        Map<String, String> csvRow = new HashMap<>();
        csvRow.put(SERVICE_POINT_ID, "89879388");
        csvRow.put(PRODUCT_NAME, "Midwifery Kit");
        csvRow.put(PRODUCT_ID, "990222");
        csvRow.put(QUANTITY, "1");
        csvRow.put(DELIVERY_DATE, "04/08/2020");
        csvRow.put(UNICEF_SECTION, "WASH");
        csvRow.put(DONOR, "ADB");
        csvRow.put(SERIAL_NUMBER, "12345");
        csvRow.put(PO_NUMBER, "897");
        csvStocks.add(csvRow);

        when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(createProductCatalogue());
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(createLocation());
        when(inventoryDataValidator.getValidDonors()).thenReturn(createDonors());
        when(inventoryDataValidator.getValidUnicefSections()).thenReturn(createUnicefSections());
        when(productCatalogueService.getProductCatalogueByName(anyString())).thenReturn(createProductCatalogue());
        CsvBulkImportDataSummary csvBulkImportDataSummary = stockService.convertandPersistInventorydata(csvStocks, "Test user");
        Assert.assertEquals(0, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
        Assert.assertEquals(csvBulkImportDataSummary.getNumberOfRowsProcessed(), csvBulkImportDataSummary.getNumberOfCsvRows());
    }

    private Inventory createInventory() {
        Date deliveryDate = new Date();
        Inventory inventory = new Inventory();
        inventory.setProductName("Midwifery Kit");
        inventory.setUnicefSection("Health");
        inventory.setDeliveryDate(deliveryDate);
        inventory.setDonor("ADB");
        inventory.setPoNumber(123);
        inventory.setSerialNumber("AX-12");
        inventory.setServicePointId("loc-1");
        inventory.setQuantity(4);
        return inventory;
    }

    private List<String> createDonors() {
        List<String> donors = new ArrayList<>();
        donors.add("ADB");
        return donors;
    }

    private List<String> createUnicefSections() {
        List<String> sections = new ArrayList<>();
        sections.add("WASH");
        return sections;
    }

    private ProductCatalogue createProductCatalogue() {
        ProductCatalogue productCatalogue = new ProductCatalogue();
        productCatalogue.setUniqueId(1l);
        productCatalogue.setProductName("Product A");
        productCatalogue.setIsAttractiveItem(Boolean.TRUE);
        productCatalogue.setMaterialNumber("MT-123");
        productCatalogue.setAvailability("available");
        productCatalogue.setCondition("good condition");
        productCatalogue.setAppropriateUsage("staff is trained to use it appropriately");
        productCatalogue.setAccountabilityPeriod(1);
        productCatalogue.setServerVersion(123456l);
        return productCatalogue;
    }

    private PhysicalLocation createLocation() {
        PhysicalLocation parentLocation = gson.fromJson(parentJson, PhysicalLocation.class);
        parentLocation.setJurisdiction(true);
        return parentLocation;
    }

    private List<String> createDonorList() {
        List<String> donors = new ArrayList<>();
        donors.add("ADB");
        donors.add("NatCom Belgium");
        donors.add("BMGF");
        donors.add("Govt of Canada");
        donors.add("NatCom Canada");
        donors.add("NatCom Denmark");
        donors.add("ECW");
        donors.add("End Violence Fund");
        donors.add("ECHO ");
        donors.add("EC ");
        donors.add("NatCom Finland ");
        donors.add("Govt of France ");
        donors.add("NatCom France ");
        donors.add("GAVI ");
        donors.add("NatCom Germany ");
        donors.add("Govt of Germany");
        donors.add("NatCom Iceland");
        donors.add("NatCom Italy");
        donors.add("Govt of Japan");
        donors.add("NatCom Japan");
        donors.add("NatCom Luxembourg");
        donors.add("Monaco");
        donors.add("NatCom Netherlands");
        donors.add("Govt of Norway");
        donors.add("NatCom Norway");
        donors.add("Nutrition Intl");
        donors.add("NatCom Poland");
        donors.add("Govt of Korea");
        donors.add("NatCom Spain");
        donors.add("NatCom Sweden");
        donors.add("NatCom Switzerland");
        donors.add("Govt of UK");
        donors.add("NatCom UK");
        donors.add("NatCom USA");
        donors.add("OFDA");
        donors.add("CDC");
        donors.add("USAID");
        donors.add("USAID FFP");
        donors.add("World Bank");
        return donors;
    }

    private List<String> createSectionsList() {
        List<String> sections = new ArrayList<>();
        sections.add("Health");
        sections.add("WASH");
        sections.add("Nutrition");
        sections.add("Education");
        sections.add("Child Protection");
        sections.add("Social Policy");
        sections.add("C4D");
        sections.add("DRR");
        return sections;
    }


}
