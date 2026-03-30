package customer.bp_risk_dashboard.handlers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;


@Component
@ServiceName("RiskService")
public class RiskSummaryHandler implements EventHandler{

    private final ObjectMapper mapper = new ObjectMapper();

    private List<Map<String,Object>> readJsonArray(String classpathLocation) {
        try (InputStream is = new ClassPathResource(classpathLocation).getInputStream()){
            return mapper.readValue(is, new TypeReference<>() {});
        }
        catch (Exception e){
            throw new RuntimeException("Failed reading"+ classpathLocation, e);
        }
    }

    @On(event = "READ", entity = "RiskService.BPRiskSummary")
    public void readSummary(CdsReadEventContext ctx){
        List<Map<String, Object>> bps = readJsonArray("mock/business-partners.json");
        List<Map<String, Object>> orders = readJsonArray("mock/sales-orders.json");
        List<Map<String, Object>> invoices = readJsonArray("mock/supplier-invoices.json");
      

        Map<String, List<Map<String, Object>>> ordersByBp = new HashMap<>();

        for (Map<String, Object> order : orders) {
            String bpId = str(order.get("SoldToParty"));
            ordersByBp.computeIfAbsent(bpId, k -> new ArrayList<>()).add(order);
        }

        Map<String, List<Map<String, Object>>> invoicesByBp = new HashMap<>();

        for (Map<String, Object> inv : invoices) {
            String bpId = str(inv.get("InvoicingParty"));
            invoicesByBp.computeIfAbsent(bpId, k -> new ArrayList<>()).add(inv);
        }

        //response

        List<Map<String,Object>> result = new ArrayList<>();

        for (Map<String,Object> bp : bps){
            String bpId = str(bp.get("BusinessPartner"));
            String fullName = str(bp.get("BusinessPartnerFullName"));

            List<Map<String,Object>> myOrders = ordersByBp.getOrDefault(bpId, new ArrayList<>());
            List<Map<String,Object>> myInvoices = invoicesByBp.getOrDefault(bpId, new ArrayList<>());

            int salesOrderCount = myOrders.size();
            int invoiceCount = myInvoices.size();

            int openOrderCount = 0;

            for(Map<String,Object> o : myOrders){
                String status = str(o.get("OverallSDProcessStatus"));

                if (!status.equals("C") && !status.isBlank()){
                    openOrderCount++;
                }
            }

            double salesTotal = 0;

            for(Map<String,Object> order : myOrders) {
                Object amt = order.get("TotalNetAmount");
                String string_amt;
                if (amt != null) {
                    string_amt = amt.toString();
                    salesTotal += Double.parseDouble(string_amt);
                }
            }

            int blockedInvoiceCount = 0;

            for (Map<String,Object> inv : myInvoices){
                String block = str(inv.get("PaymentBlockingReason"));

                if (!block.isBlank()){
                    blockedInvoiceCount++;
                }        
            }

            int totalDays = 0;
            int countDays = 0;

            for (Map<String,Object> inv : myInvoices){
                Object daysObj = inv.get("NetPaymentDays");
                String string_days;
                if(daysObj != null){
                    string_days = daysObj.toString();
                    totalDays += Integer.parseInt(string_days);
                    countDays++;
                }
            }

            double avgNetPaymentDays = 0;
            if (countDays > 0){
                avgNetPaymentDays = (double) totalDays / countDays;
            }

            int riskPoints = 0;

            if (openOrderCount > 1) riskPoints += 2;
            if (salesTotal > 3000000) riskPoints += 1;
            if (blockedInvoiceCount > 0) riskPoints += 3;
            if (avgNetPaymentDays > 60) riskPoints += 3;

            int riskScore = Math.min(riskPoints * 10, 100);

            row.put("RiskPoints", riskPoints);
            row.put("RiskScore", riskScore);

            Map<String,Object> row = new LinkedHashMap<>();
            row.put("BusinessPartner", bpId);
            row.put("BusinessPartnerFullName", fullName);
            row.put("SalesOrderCount", salesOrderCount);
            row.put("InvoiceCount", invoiceCount);
            row.put("OpenOrderCount", openOrderCount);
            row.put("SalesTotal", salesTotal);
            row.put("BlockedInvoiceCount", blockedInvoiceCount);
            row.put("AvgNetPymntDays", avgNetPaymentDays);


            result.add(row);
        }

        ctx.setResult(result);
    }


    private static String str(Object o){
        return o == null ? "" : String.valueOf(o).trim();
    }
    

}
