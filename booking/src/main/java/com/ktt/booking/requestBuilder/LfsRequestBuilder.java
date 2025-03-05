// package com.ktt.booking.requestBuilder;

// import com.ktt.booking.dto.SearchDto;
// import org.springframework.stereotype.Component;

// @Component
// public class LfsRequestBuilder {

// 	public String buildSoapRequest(SearchDto searchRequest) {
// 		StringBuilder soapRequestXml = new StringBuilder();

// 		soapRequestXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ses=\"http://www.travelport.com/soa/common/security/SessionContext_v1\" xmlns:air=\"http://www.travelport.com/schema/air_v51_0\" xmlns:com=\"http://www.travelport.com/schema/common_v51_0\">")
// 				.append("<soapenv:Body>");

// 		soapRequestXml.append("<air:LowFareSearchReq TraceId=\"a0a168a8-1ad3-44d5-b70b-1ccec46b81e8\" TargetBranch=\"P7232098\" SolutionResult=\"false\" AuthorizedBy=\"TBO\">");
// 		soapRequestXml.append("<com:BillingPointOfSaleInfo OriginApplication=\"uAPI\" /><air:SearchAirLeg>");
// 		soapRequestXml.append("<air:SearchOrigin><com:CityOrAirport Code=\"").append(searchRequest.getOrigin()).append("\"/></air:SearchOrigin>");
// 		soapRequestXml.append("<air:SearchDestination><com:CityOrAirport Code=\"").append(searchRequest.getDestination()).append("\"/></air:SearchDestination>");
// 		soapRequestXml.append("<air:SearchDepTime PreferredTime=\"").append(searchRequest.getFromDate()).append("\"/>");
// 		if (searchRequest.getToDate() != null) {
// 			soapRequestXml.append("<air:SearchArvTime PreferredTime=\"").append(searchRequest.getToDate()).append("\"/>");;
// 		}
// 		soapRequestXml.append("<air:AirLegModifiers></air:AirLegModifiers></air:SearchAirLeg>");
// 		soapRequestXml.append("""
// 				<air:AirSearchModifiers>
// 				        <air:PreferredProviders>
// 				          <com:Provider Code="1G" />
// 				        </air:PreferredProviders>
// 				      </air:AirSearchModifiers>
// 				      <com:SearchPassenger Code="ADT" />
// 				      <air:AirPricingModifiers ETicketability="Yes" />
// 				      <air:FareRulesFilterCategory>
// 				        <air:CategoryCode>CHG</air:CategoryCode>
// 				      </air:FareRulesFilterCategory>
// 				    </air:LowFareSearchReq>
// 				  </soapenv:Body>
// 				</soapenv:Envelope>""");
// 		System.out.println(soapRequestXml);
// 		return soapRequestXml.toString();
// 	}
// }

package com.ktt.booking.requestBuilder;

import com.ktt.booking.dto.SearchDto;
import org.springframework.stereotype.Component;

@Component
public class LfsRequestBuilder {

	public String buildSoapRequest(SearchDto searchRequest) {
		StringBuilder soapRequestXml = new StringBuilder();

		soapRequestXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ses=\"http://www.travelport.com/soa/common/security/SessionContext_v1\" xmlns:air=\"http://www.travelport.com/schema/air_v51_0\" xmlns:com=\"http://www.travelport.com/schema/common_v51_0\">")
				.append("<soapenv:Body>");

		soapRequestXml.append("<air:LowFareSearchReq TraceId=\"a0a168a8-1ad3-44d5-b70b-1ccec46b81e8\" TargetBranch=\"P7232098\" SolutionResult=\"false\" AuthorizedBy=\"TBO\">");
		soapRequestXml.append("<com:BillingPointOfSaleInfo OriginApplication=\"uAPI\" />");
		soapRequestXml.append("<air:SearchAirLeg><air:SearchOrigin><com:Airport Code=\"").append(searchRequest.getOrigin()).append("\"/></air:SearchOrigin>");
		soapRequestXml.append("<air:SearchDestination><com:Airport Code=\"").append(searchRequest.getDestination()).append("\"/></air:SearchDestination>");
		soapRequestXml.append("<air:SearchDepTime PreferredTime=\"").append(searchRequest.getFromDate()).append("\"/></air:SearchAirLeg>");
		if (searchRequest.getToDate() != null) {
			soapRequestXml.append("<air:SearchAirLeg><air:SearchOrigin><com:Airport Code=\"").append(searchRequest.getDestination()).append("\"/></air:SearchOrigin>");
			soapRequestXml.append("<air:SearchDestination><com:Airport Code=\"").append(searchRequest.getOrigin()).append("\"/></air:SearchDestination>");
			soapRequestXml.append("<air:SearchDepTime PreferredTime=\"").append(searchRequest.getToDate()).append("\"/></air:SearchAirLeg>");
		}
		soapRequestXml.append("""
				<air:AirSearchModifiers>
				        <air:PreferredProviders>
				          <com:Provider Code="1G" />
				        </air:PreferredProviders>
				      </air:AirSearchModifiers>""");

		if (searchRequest.getAdults() > 0) {
			soapRequestXml.append(travellerSnippet("ADT",searchRequest.getAdults()));
		}
		if (searchRequest.getInfants() > 0) {
			soapRequestXml.append(travellerSnippet("INF",searchRequest.getInfants()));
		}
		if (searchRequest.getChildren() > 0) {
			soapRequestXml.append(travellerSnippet("CHD",searchRequest.getChildren()));
		}
		soapRequestXml.append(""" 
				      <air:AirPricingModifiers FaresIndicator="AllFares" ETicketability="Yes" />
				      <air:FareRulesFilterCategory>
				        <air:CategoryCode>CHG</air:CategoryCode>
				      </air:FareRulesFilterCategory>
				    </air:LowFareSearchReq>
				  </soapenv:Body>
				</soapenv:Envelope>""");
		return soapRequestXml.toString();
	}

	public String travellerSnippet(String code,int count){
		StringBuilder stringBuilder = new StringBuilder();
		if (code.equals("ADT")){
			stringBuilder.append("<com:SearchPassenger Code=\"ADT\" />".repeat(Math.max(0, count)));
		}
		if (code.equals("INF")){
			stringBuilder.append("<com:SearchPassenger Code=\"INF\" Age=\"1\" />".repeat(Math.max(0, count)));
		}
		if (code.equals("CHD")){
			for(int i=2;i<count+2;i++)
				stringBuilder.append("<com:SearchPassenger Code=\"CHD\" Age=\"").append(i).append("\" />");
		}
		return stringBuilder.toString();
	}
}
