package pl.bykowski.rectangleapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.bykowski.rectangleapp.model.Debtor;
import pl.bykowski.rectangleapp.model.dto.DebtorDTO;
import pl.bykowski.rectangleapp.model.dto.DebtorDetailsDTO;
import pl.bykowski.rectangleapp.model.dto.DebtorHistoryDTO;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CurrencyService {

    private RestTemplate restTemplate;

    public CurrencyService(){
        this.restTemplate = new RestTemplate();
    }

    public String calculateCurrencyRates(String currencyOne, String currencyTwo){

        if(currencyOne == null){
            currencyOne = "PLN";
        }

        JsonNode jsonNode;

        //TODO this is wrong way, you should use ur own implementation of something like resthandler? (google it) from spring
        try{
            jsonNode = restTemplate.getForObject("https://api.exchangerate-api.com/v4/latest/" +
                    currencyOne.toUpperCase(), JsonNode.class).get("rates")
                    .get(currencyTwo.toUpperCase());
            return  jsonNode.asText();
        }catch (Exception ex){
            return "1";
        }
    }

    //TODO looks like there is chances to use generic
    public List<Debtor> setCurrencyRateForDebtors(List<Debtor> debtors, String currencyRate) {

//        if(Double.parseDouble(currencyRate) <= 1){
//            debtors.stream()
//                    .forEach(n -> {
//                        n.setTotalDebt(n.getTotalDebt().multiply(new BigDecimal(currencyRate)));
//                    });
//            return debtors;
//        }
            debtors.stream()
                    .forEach(n -> {
                        n.setTotalDebt(n.getTotalDebt().divide(new BigDecimal(currencyRate)));
                    });
        return debtors;
    }

    public List<DebtorDetailsDTO> setCurrencyRateForDebtorDetailsDTO(List<DebtorDetailsDTO> debtorDetailsDTOList1, String currencyRate) {
        debtorDetailsDTOList1.stream()
                .forEach(n -> {
                    n.setDebt(n.getDebt().multiply(new BigDecimal(currencyRate)));
                });
        return debtorDetailsDTOList1;
    }

    public List<DebtorHistoryDTO> setCurrencyRateForDebtorHistoryDTO(List<DebtorHistoryDTO> debtorHistoryDTOS, String currencyRate) {
        debtorHistoryDTOS.stream()
                .forEach(n -> {
                    n.setDebt(n.getDebt().multiply(new BigDecimal(currencyRate)));
                });
        return debtorHistoryDTOS;
    }
}
