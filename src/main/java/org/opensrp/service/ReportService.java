package org.opensrp.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensrp.domain.Hia2Indicator;
import org.opensrp.domain.Report;
import org.opensrp.repository.ReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

	private final ReportsRepository allReports;

	private static Logger logger = LogManager.getLogger(ReportService.class.toString());
	
	@Autowired
	public ReportService(ReportsRepository allReports) {
		this.allReports = allReports;
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> findAllByIdentifier(String identifier) {
		return allReports.findAllByIdentifier(identifier);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> findByServerVersion(long serverVersion) {
		return allReports.findByServerVersion(serverVersion);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public Report getById(String id) {
		return allReports.findById(id);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> getAll() {
		return allReports.getAll();
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public Report find(String uniqueId) {
		List<Report> reportList = allReports.findAllByIdentifier(uniqueId);
		if (reportList.size() > 1) {
			throw new IllegalArgumentException("Multiple reports with identifier " + uniqueId + " exist.");
		} else if (reportList.size() != 0) {
			return reportList.get(0);
		}
		return null;
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public Report find(Report report) {
		for (String idt : report.getIdentifiers().keySet()) {
			List<Report> reportList = allReports.findAllByIdentifier(report.getIdentifier(idt));
			if (reportList.size() > 1) {
				throw new IllegalArgumentException(
						"Multiple reports with identifier type " + idt + " and ID " + report.getIdentifier(idt) + " exist.");
			} else if (reportList.size() != 0) {
				return reportList.get(0);
			}
		}
		return null;
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public Report findById(String reportId) {
		try {
			if (reportId == null || reportId.isEmpty()) {
				return null;
			}
			return allReports.findById(reportId);
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public synchronized Report addReport(Report report) {
		Report existingReport = find(report);
		if (existingReport != null) {
			throw new IllegalArgumentException(
					"An report already exists with given list of identifiers. Consider updating data.[" + existingReport
							+ "]");
		}

		if (report.getFormSubmissionId() != null
				&& getByBaseEntityAndFormSubmissionId(report.getBaseEntityId(), report.getFormSubmissionId()) != null) {
			throw new IllegalArgumentException(
					"An report already exists with given baseEntity and formSubmission combination. Consider updating");
		}

		report.setDateCreated(DateTime.now());
		allReports.add(report);
		return report;
	}

	public void updateReport(Report updatedReport) {
		// If update is on original entity
		if (updatedReport.isNew()) {
			throw new IllegalArgumentException(
					"Report to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}

		updatedReport.setDateEdited(DateTime.now());
		allReports.update(updatedReport);
	}

	@PreAuthorize("hasRole('REPORT_CREATE') or hasRole('REPORT_UPDATE')")
	public synchronized Report addorUpdateReport(Report report) {
		Report existingReport = findById(report.getId());
		if (existingReport != null) {
			report.setDateEdited(DateTime.now());
			report.setRevision(existingReport.getRevision());
			allReports.update(report);
		} else {
			report.setDateCreated(DateTime.now());
			allReports.add(report);
		}

		return report;
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public Report getByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		List<Report> reportList = allReports.findByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
		if (reportList.size() > 1) {
			throw new IllegalStateException("Multiple reports for baseEntityId and formSubmissionId combination ("
					+ baseEntityId + "," + formSubmissionId + ")");
		}
		if (reportList.size() == 0) {
			return null;
		}
		return reportList.get(0);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> findByBaseEntityId(String baseEntityId) {
		return allReports.findByBaseEntityId(baseEntityId);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> findReports(String team, String providerId, String locationId, Long serverVersion, String sortBy,
			String sortOrder, int limit) {
		return allReports.findReports(team, providerId, locationId, null, serverVersion, sortBy, sortOrder, limit);
	}

	@PreAuthorize("hasRole('REPORT_VIEW')")
	public List<Report> findReports(String team, String providerId, String locationId, String baseEntityId,
			Long serverVersion, String sortBy, String sortOrder, int limit) {
		return allReports.findReports(team, providerId, locationId, baseEntityId, serverVersion, sortBy, sortOrder, limit);
	}

	public List<Report> findReports(String baseEntityId, DateTime from, DateTime to, String reportType,
			String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo) {

		return allReports.findReports(baseEntityId, from, to, reportType, providerId, locationId, lastEditFrom, lastEditTo);
	}

	/**
	 * Initialize specific vaccines section report
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initSpecificIndicators() {
		return new LinkedHashMap<>() {{
			put("BCG", null);
			put("ECV", null);
			put("EPNT", null);
			put("FJaune", null);
			put("HepBnaissance", null);
			put("IEC", null);
			put("MAPI", null);
			put("MenA", null);
			put("PVH1", null);
			put("PVH2", null);
			put("Penta_1", null);
			put("Penta_2", null);
			put("Penta_3", null);
			put("Pneumo_1", null);
			put("Pneumo_2", null);
			put("Pneumo_3", null);
			put("RR_1", null);
			put("RR_2", null);
			put("Rota_1", null);
			put("Rota_2", null);
			put("SAB_0_05", null);
			put("SAB_0_5", null);
			put("SAB_2", null);
			put("SAB_5", null);
			put("Td-1FAP", null);
			put("Td-1FE", null);
			put("Td-2FAP", null);
			put("Td-2FE", null);
			put("VAA", null);
			put("VPI", null);
			put("VPO_0", null);
			put("VPO_1", null);
			put("VPO_2", null);
			put("VPO_3", null);
			put("VitA", null);
		}};
	}

	/**
	 * Initialize aggregated vaccines section report
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initAggregatedIndicators() {
		return new LinkedHashMap<>() {{
			put("BCG", null);
			put("Bte", null);
			put("Cartes", null);
			put("ECV", null);
			put("EPNT", null);
			put("FJaune", null);
			put("HepBnaissance", null);
			put("IEC", null);
			put("MAPI", null);
			put("MenA", null);
			put("PVH", null);
			put("Penta", null);
			put("Pneumo", null);
			put("RR", null);
			put("Rota", null);
			put("SAB_0_05", null);
			put("SAB_0_5", null);
			put("SAB_2", null);
			put("SAB_5", null);
			put("Td", null);
			put("VAA", null);
			put("VPI", null);
			put("VPO", null);
			put("VitA", null);
		}};
	}

	/**
	 * Initialize stock section report
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initStockIndicators() {
		return new LinkedHashMap<>() {{
			put("BCG", null);
			put("HepB", null);
			put("VPO", null);
			put("VPI", null);
			put("Penta", null);
			put("Pneumo", null);
			put("Rota", null);
			put("RR", null);
			put("VAA", null);
			put("MenA", null);
			put("HPV", null);
			put("Td", null);
			put("SAB_0_05", null);
			put("SAB_0_5", null);
			put("SAB_2", null);
			put("SAB_5", null);
		}};
	}

	/**
	 * Initialize specific vaccines section report indicator with value entries
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initSpecificIndicatorsSection() {
		Map<String, Map<String, Object>> sectionReport = initSpecificIndicators();

		for (Map.Entry<String, Map<String, Object>> entry : sectionReport.entrySet()) {
			sectionReport.replace(entry.getKey(), new HashMap<>() {{
				// put("identifier", ""); // substring in indicatorCode used to identify subcategory
				put("fixed_m", 0);
				put("fixed_f", 0);
				put("mobile_m", 0);
				put("mobile_f", 0);
				put("grand_total", 0);
				put("grand_total_m", 0);
				put("grand_total_f", 0);
				put("out_of_area_total", 0);
				put("out_of_area_m", 0);
				put("out_of_area_f", 0);
				put("out_of_tranche_total", 0);
				put("out_of_tranche_m", 0);
				put("out_of_tranche_f", 0);
			}});
		}

		return sectionReport;
	}

	/**
	 * Initialize aggregated vaccines section report indicator with value entries
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initAggregatedIndicatorsSection() {
		Map<String, Map<String, Object>> sectionReport = initAggregatedIndicators();

		for (Map.Entry<String, Map<String, Object>> entry : sectionReport.entrySet()) {
			sectionReport.replace(entry.getKey(), new HashMap<>() {{
				put("stock_start_of_month", 0);
				put("stock_received_during_month", 0);
				put("stock_end_of_month", 0);
				put("qty_used", 0);
				put("qty_administered", 0);
				put("qty_lost", 0);
				put("loss_expired", 0);
				put("loss_freezing", 0);
				put("loss_heat", 0);
				put("loss_other", 0);
				put("loss_of_use", 0);
				put("days_rupture", 0);
				put("main_causes", 0);
			}});
		}

		return sectionReport;
	}

	/**
	 * Initialize stock section report indicator with value entries
	 *
	 * @return
	 */
	private Map<String, Map<String, Object>> initStockIndicatorsSection() {
		Map<String, Map<String, Object>> sectionReport = initStockIndicators();

		for (Map.Entry<String, Map<String, Object>> entry : sectionReport.entrySet()) {
			sectionReport.replace(entry.getKey(), new LinkedHashMap<>() {{
				put("stock_start", 0);
				put("stock_received", 0);
				put("remaining_stock", 0);
				put("loss", 0);
				put("break_days", 0);
			}});
		}

		return sectionReport;
	}

	/**
	 * Load values into specific vaccines section report
	 *
	 * @param reports List of Hia2 reports
	 * @return
	 */
	public Map<String, Map<String, Object>> populateSpecificIndicatorsSection(List<Report> reports) {
		Map<String, Map<String, Object>> sectionReport = initSpecificIndicatorsSection();

		for (Report report : reports) {
			List<Hia2Indicator> reportIndicators = report.getHia2Indicators();

			for (Hia2Indicator item : reportIndicators) {
				int pos = item.getIndicatorCode().indexOf("_");
				String code = item.getIndicatorCode().substring(0, (pos < 0 ? item.getIndicatorCode().length() : pos));

				switch (code) {
					case "BCG":
					case "ECV":
					case "EPNT":
					case "HepBnaissance":
					case "MenA":
					case "Penta": // 1,2,3
					case "Pneumo": // 1,2,3
					case "RR": // 1,2
					case "Rota": // 1,2
					case "SAB":// 0_05ml,0_5ml,2ml,5ml
					case "VAA":
					case "VPI":
					case "VPO": // 1,2,3
					case "VitA":
						break;
					case "PVH1":
					case "PVH2":
						code = "PVH";
						break;
					case "Td-1FAP":
					case "Td-1FE":
					case "Td-2FAP":
					case "Td-2FE":
						code = "Td";
						break;
					default:
						break;
				}

				processSpecificIndicator(code, item, sectionReport);
			}
		}

		return sectionReport;
	}

	/**
	 * Load values into aggregated vaccines section report
	 *
	 * @param reports List of Hia2 reports
	 * @return
	 */
	public Map<String, Map<String, Object>> populateAggregatedIndicatorsSection(List<Report> reports) {
		Map<String, Map<String, Object>> sectionReport = initAggregatedIndicatorsSection();

		for (Report report : reports) {
			List<Hia2Indicator> reportIndicators = report.getHia2Indicators();

			for (Hia2Indicator item : reportIndicators) {
				int pos = item.getIndicatorCode().indexOf("_");
				String code = item.getIndicatorCode().substring(0, (pos < 0 ? item.getIndicatorCode().length() : pos));

				switch (code) {
					case "BCG":
					case "Bte":
					case "Cartes":
					case "ECV":
					case "EPNT":
					case "FJaune":
					case "HepBnaissance":
					case "PVH":
					case "Td-1FAP":
					case "Td-1FE":
					case "Td-2FAP":
					case "Td-2FE":
					case "VAA":
					case "VPI":
					case "VitA":
					case "MenA":
					case "MAPI":
						break;
					case "HepB":
						code = "HepBnaissance";
						break;
					case "Men":
						code = "MenA";
						break;
					case "Penta": // 1,2,3
					case "Pneumo": // 1,2,3
					case "RR": // 1,2
					case "Rota": // 1,2
					case "VPO": // 0,1,2,3
						if (item.getIndicatorCode().indexOf(code + "_0") >= 0
								|| item.getIndicatorCode().indexOf(code + "_1") >= 0
								|| item.getIndicatorCode().indexOf(code + "_2") >= 0
								|| item.getIndicatorCode().indexOf(code + "_3") >= 0) {
							continue;
						}
						break;
					case "SAB":
						if (item.getIndicatorCode().indexOf(code + "_0_05") >= 0) {
							code = "SAB_0_05";
						} else if (item.getIndicatorCode().indexOf(code + "_0_5") >= 0) {
							code = "SAB_0_5";
						} else if (item.getIndicatorCode().indexOf(code + "_2") >= 0) {
							code = "SAB_2";
						} else if (item.getIndicatorCode().indexOf(code + "_5") >= 0) {
							code = "SAB_5";
						}
						// TODO: ignore SAB items for now
						continue;
					default:
						break;
				}

				processAggregatedIndicator(code, item, sectionReport);
			}
		}

		return sectionReport;
	}

	/**
	 * Load values into stock section report
	 *
	 * @param reports List of Hia2 reports
	 * @return
	 */
	public Map<String, Map<String, Object>> populateStockIndicatorsSection(List<Report> reports) {
		Map<String, Map<String, Object>> sectionReport = initStockIndicatorsSection();

		for (Report report : reports) {
			List<Hia2Indicator> reportIndicators = report.getHia2Indicators();

			for (Hia2Indicator item : reportIndicators) {
				int pos = item.getIndicatorCode().indexOf("_");
				String code = item.getIndicatorCode().substring(0, (pos < 0 ? item.getIndicatorCode().length() : pos));

				switch (code) {
					case "BCG":
					case "HepB":
					case "VPO":
					case "VPI":
					case "Penta":
					case "Pneumo":
					case "Rota":
					case "RR":
					case "VAA":
					case "MenA":
					case "HPV":
					case "Td":
					case "SAB_0_05":
					case "SAB_0_5":
					case "SAB_2":
					case "SAB_5":
						break;
					default:
						break;
				}

				processStockIndicator(code, item, sectionReport);
			}
		}

		return sectionReport;
	}

	/**
	 * Update specific vaccines section report with the indicators data
	 *
	 * @param code          Indicator code prefix (text up to the first underscore)
	 * @param indicator     The Hia2Indicator
	 * @param sectionReport Report section - each main district sheet is divided into 3 sections and 2 sections for the facility sheets.
	 *                      Individual vaccine subcategories, totals for the vaccine subcategories and the stock section
	 */
	private void processSpecificIndicator(String code, Hia2Indicator indicator,
			Map<String, Map<String, Object>> sectionReport) {
		Map<String, Object> values = sectionReport.get(code);
		String indicatorCode = indicator.getIndicatorCode();

		if (values == null) {
			return;
		}

		if (indicatorCode.toLowerCase().indexOf("m_mobile") >= 0) {
			values.replace("mobile_m",
					((int) values.getOrDefault("mobile_m", 0)
							+ Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.toLowerCase().indexOf("f_mobile") >= 0) {
			values.replace("mobile_f",
					((int) values.getOrDefault("mobile_f", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
			// TODO: seek clarification & update checks for all subcategories
			/*
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("fixed_m",
					((int) values.getOrDefault("fixed_m", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("fixed_f",
					((int) values.getOrDefault("fixed_f", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("grand_total",
					((int) values.getOrDefault("grand_total", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("grand_total_m",
					((int) values.getOrDefault("grand_total_m", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("grand_total_f",
					((int) values.getOrDefault("grand_total_f", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_area_total",
					((int) values.getOrDefault("out_of_area_total", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_area_m",
					((int) values.getOrDefault("out_of_area_m", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_area_f",
					((int) values.getOrDefault("out_of_area_f", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_tranche_total",
					((int) values.getOrDefault("out_of_tranche_total", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_tranche_m",
					((int) values.getOrDefault("out_of_tranche_m", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
		} else if (indicatorCode.indexOf("xxx") >= 0) {
			values.replace("out_of_tranche_f",
					((int) values.getOrDefault("out_of_tranche_f", 0) + Integer.parseInt(indicator.getValue())));
			report.replace(code, values);
			 */
		}
	}

	/**
	 * Update aggregated vaccines section report with the indicators data
	 *
	 * @param code          Indicator code prefix (text up to the first underscore)
	 * @param indicator     The Hia2Indicator
	 * @param sectionReport Report section - each main district sheet is divided into 3 sections and 2 sections for the facility sheets.
	 *                      Individual vaccine subcategories, totals for the vaccine subcategories and the stock section
	 */
	private void processAggregatedIndicator(String code, Hia2Indicator indicator,
			Map<String, Map<String, Object>> sectionReport) {
		Map<String, Object> values = sectionReport.get(code);

		if (values == null) {
			return;
		}

		String indicatorCode = indicator.getIndicatorCode();

		if (indicatorCode.indexOf("Used") >= 0) {
			values.replace("qty_used", ((int) values.getOrDefault("qty_used", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Administered") >= 0) {
			values.replace("qty_administered",
					((int) values.getOrDefault("qty_administered", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Beginning_Balance") >= 0) {
			values.replace("stock_start_of_month",
					((int) values.getOrDefault("stock_start_of_month", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Ending_Balance") >= 0) {
			values.replace("stock_end_of_month",
					((int) values.getOrDefault("stock_end_of_month", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_Expired") >= 0) {
			values.replace("loss_expired",
					((int) values.getOrDefault("loss_expired", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_Freezing") >= 0) {
			values.replace("loss_freezing",
					((int) values.getOrDefault("loss_freezing", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_Heating") >= 0) {
			values.replace("loss_heat",
					((int) values.getOrDefault("loss_heat", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_Other") >= 0) {
			values.replace("loss_other",
					((int) values.getOrDefault("loss_other", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_of_Use") >= 0) {
			values.replace("loss_of_use",
					((int) values.getOrDefault("loss_of_use", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Lost") >= 0) {
			values.replace("qty_lost", ((int) values.getOrDefault("qty_lost", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Qty_Received") >= 0) {
			values.replace("stock_received_during_month",
					((int) values.getOrDefault("stock_received_during_month", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Stockout_Days") >= 0) {
			values.replace("days_rupture",
					((int) values.getOrDefault("days_rupture", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Stockout_Main_Causes") >= 0) {
			values.replace("main_causes",
					((int) values.getOrDefault("main_causes", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		}
	}

	/**
	 * Update stock section report with the indicators data
	 *
	 * @param code          Indicator code prefix (text up to the first underscore)
	 * @param indicator     The Hia2Indicator
	 * @param sectionReport Report section - each main district sheet is divided into 3 sections and 2 sections for the facility sheets.
	 *                      Individual vaccine subcategories, totals for the vaccine subcategories and the stock section
	 */
	private void processStockIndicator(String code, Hia2Indicator indicator,
			Map<String, Map<String, Object>> sectionReport) {
		Map<String, Object> values = sectionReport.get(code);
		String indicatorCode = indicator.getIndicatorCode();

		if (values == null) {
			return;
		}

		if (indicatorCode.indexOf("Beginning_Balance") >= 0) {
			values.replace("stock_start",
					((int) values.getOrDefault("stock_start", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Qty_Received") >= 0) {
			values.replace("stock_received",
					((int) values.getOrDefault("stock_received", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Ending_Balance") >= 0) {
			values.replace("remaining_stock",
					((int) values.getOrDefault("remaining_stock", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Loss_Expired") >= 0
				|| indicatorCode.indexOf("Loss_Freezing") >= 0
				|| indicatorCode.indexOf("Loss_Heating") >= 0
				|| indicatorCode.indexOf("Loss_Other") >= 0
				|| indicatorCode.indexOf("Loss_of_Use") >= 0) {

			values.replace("loss",
					((int) values.getOrDefault("loss", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		} else if (indicatorCode.indexOf("Stockout_Days") >= 0
				|| indicatorCode.indexOf("Stockout_Main_Causes") >= 0) {

			values.replace("break_days",
					((int) values.getOrDefault("break_days", 0) + Integer.parseInt(indicator.getValue())));
			sectionReport.replace(code, values);
		}
	}
}
