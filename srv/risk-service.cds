using { API_BUSINESS_PARTNER as BP } from './src/main/resources/external/API_BUSINESS_PARTNER';
using { API_SALES_ORDER_SRV as SO } from './src/main/resources/external/API_SALES_ORDER_SRV';
using { API_SUPPLIERINVOICE_PROCESS_SRV as INV } from './src/main/resources/external/API_SUPPLIERINVOICE_PROCESS_SRV';

service RiskService {

  entity RemoteBusinessPartners as projection on BP.A_BusinessPartner{
    BusinessPartner,
    BusinessPartnerFullName,
    BusinessPartnerCategory,
    BusinessPartnerGrouping,
    OrganizationBPName1,
    OrganizationBPName2,
    SearchTerm1,
    SearchTerm2,
    CreatedByUser,
    CreationDate,
    LastChangeDate
  };

  entity RemoteSalesOrders as projection on SO.A_SalesOrder{
    SalesOrder,
    SalesOrderType,
    DistributionChannel,
    OrganizationDivision,
    SoldToParty,
    CustomerPurchaseOrderType,
    PurchaseOrderByCustomer,
    SalesOrderDate,
    CreatedByUser,
    CreationDate,
    LastChangeDate,
    TotalNetAmount,
    TransactionCurrency,
    OverallSDProcessStatus,
  
   businessPartner : Association to RemoteBusinessPartners
    on businessPartner.BusinessPartner = SoldToParty
  };

  entity RemoteSupplierInvoices as projection on INV.A_SupplierInvoice {
    SupplierInvoice,
    CompanyCode,
    DocumentDate,
    PostingDate,
    FiscalYear,
    InvoiceGrossAmount,
    DocumentCurrency,
    PaymentBlockingReason,
    SupplierInvoiceStatus,
    InvoicingParty,
    NetPaymentDays,

    businessPartner : Association to RemoteBusinessPartners
    on businessPartner.BusinessPartner = InvoicingParty
  }

  entity BPRiskSummary {
    key BusinessPartner : String(10);
    BusinessPartnerFullName : String(81);

    SalesOrderCount : Integer;
    OpenOrderCount : Integer;
    SalesTotal : Decimal(16,3);

    InvoiceCount : Integer;
    BlockedInvoiceCount : Integer;
    AvgNetPymntDays : Decimal(9,2);

    RiskPoints : Integer;
    RiskScore : Integer;
  };
  
}
