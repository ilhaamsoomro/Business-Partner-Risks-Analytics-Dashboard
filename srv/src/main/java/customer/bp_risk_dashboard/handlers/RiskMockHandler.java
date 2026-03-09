package customer.bp_risk_dashboard.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@ServiceName("RiskService")
public class RiskMockHandler implements EventHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @On(event = "READ", entity = "RiskService.RemoteBusinessPartners")
    public void readBusinessPartners(CdsReadEventContext ctx) {
        ctx.setResult(readJsonArray("mock/business-partners.json"));
    }

    @On(event = "READ", entity = "RiskService.RemoteSalesOrders")
    public void readSalesOrders(CdsReadEventContext ctx) {
        ctx.setResult(readJsonArray("mock/sales-orders.json"));
    }

    @On(event = "READ", entity = "RiskService.RemoteSupplierInvoices")
    public void readSupplierInvoices(CdsReadEventContext ctx) {
        ctx.setResult(readJsonArray("mock/supplier-invoices.json"));
    }

    private List<Map<String, Object>> readJsonArray(String classpathLocation) {
        try (InputStream is = new ClassPathResource(classpathLocation).getInputStream()) {
            return mapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed reading " + classpathLocation, e);
        }
    }
}
