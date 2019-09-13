package pl.bykowski.rectangleapp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bykowski.rectangleapp.model.dto.DebtorDetailsDTO;
import pl.bykowski.rectangleapp.repositories.DebtorRepo;
import pl.bykowski.rectangleapp.model.Debtor;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Service
public class DebtorService {

    private DebtorRepo debtorRepo;
    private UserService userService;
    private DebtorDetailsService debtorDetailsService;

    public DebtorService(DebtorRepo debtorRepo, UserService userService,
                        DebtorDetailsService debtorDetailsService) {
        this.debtorRepo = debtorRepo;
        this.userService = userService;
        this.debtorDetailsService = debtorDetailsService;
    }

    private void saveDebtor(Debtor debtor){
        debtorRepo.save(debtor);
    }

    public Debtor findById(Long id){
        return debtorRepo.findById(id).get();
    }

    private boolean isThisNameFree(String debtorName, String userName) {
        List<Debtor> debtorList = debtorRepo.findByUserName(userName);

        for (Debtor debtor : debtorList) {
            if (debtor.getName().equalsIgnoreCase(debtorName)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void updateTotalDebtAndMakeNewDebtorDetails(DebtorDetailsDTO debtorDetails, Debtor debtor, String userName){
        debtorDetailsService.addNewDebtorDetails(debtorDetails.getName(), debtorDetails.getDebt(), debtorDetails.getReasonForTheDebt(), userName, debtor);
        updateTotalDebt(debtor.getName(), debtorDetails.getDebt(), userName);
    }

    public void updateTotalDebt(String debtorName, float debtValue, String userName) {
        Debtor changedDebtor = debtorRepo.findByName(debtorName);
        float newDebt = debtValue + changedDebtor.getTotalDebt();
        changedDebtor.setTotalDebt(newDebt);
        changedDebtor.setUserName(userName);
        saveDebtor(changedDebtor);
    }

    public void addNewDebtor(String debtorName, float debtValue, String reasonForTheDebt, String userName) {
        String actualUserName = userService.findUserName();
        if (isThisNameFree(debtorName, actualUserName)) {
            Debtor debtor = new Debtor();
            debtor.setName(debtorName);
            debtor.setTotalDebt((debtValue + debtor.getTotalDebt()));
            debtor.setDate(LocalDate.now());
            debtor.setUserName(userName);
            debtorRepo.save(debtor);

            debtorDetailsService.addNewDebtorDetails(debtorName, debtValue, reasonForTheDebt, userName, debtor);
        }
    }

    public Debtor returnDebtorWithBiggestDebt(Principal principal) {
        Optional<Debtor> debtorWithBiggestDebt = debtorRepo.findByUserName(principal.getName()).stream().
                max(Comparator.comparing(Debtor::getTotalDebt));
        System.out.println(debtorWithBiggestDebt.get().getTotalDebt());
        return debtorWithBiggestDebt.get();
    }

    public Debtor findDebtorByName(String name) {
        return debtorRepo.findByName(name);
    }
}
