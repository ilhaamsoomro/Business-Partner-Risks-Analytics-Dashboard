namespace risk;

entity BusinessPartners {
    key ID : UUID;
    bpId : String(20);
    companyName : String(111);
    country : String(3);
    creditScore : Integer;
    risklevel : String(10);
    createdAt : Timestamp
}