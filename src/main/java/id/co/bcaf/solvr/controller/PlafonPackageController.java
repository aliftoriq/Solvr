package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.plafon.PlafonPackageRequest;
import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.services.PlafonPackageService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plafon")
public class PlafonPackageController {
    @Autowired
    private PlafonPackageService plafonPackageService;

    @Secured("PLAFON_CREATE")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlafonPackageRequest dto) {
        PlafonPackage plafon = new PlafonPackage();
        plafon.setName(dto.getName());
        plafon.setAmount(dto.getAmount());
        plafon.setMaxTenorMonths(dto.getMaxTenorMonths());
        plafon.setInterestRate(dto.getInterestRate());

        PlafonPackage result = plafonPackageService.createPlafonPackage(plafon);

        return ResponseEntity.ok(new ResponseTemplate(200, "Succes",result));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        List<PlafonPackage> result = plafonPackageService.getAllPlafonPackages();

        return ResponseEntity.ok(new ResponseTemplate(200, "Succes",result));
    }

    @Secured("PLAFON_UPDATE")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PlafonPackageRequest dto) {
        PlafonPackage plafon = new PlafonPackage();
        plafon.setId(id);
        plafon.setName(dto.getName());
        plafon.setAmount(dto.getAmount());
        plafon.setLevel(dto.getLevel());
        plafon.setMaxTenorMonths(dto.getMaxTenorMonths());
        plafon.setInterestRate(dto.getInterestRate());

        PlafonPackage result = plafonPackageService.updatePlafonPackage(plafon);

        return ResponseEntity.ok(new ResponseTemplate(200, "Succes",result));
    }

    @Secured("PLAFON_DELETE")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        plafonPackageService.deletePlafonPackage(id);
        return ResponseEntity.ok(new ResponseTemplate(200, "Succes",null));
    }
}
