package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    @Autowired
    private AdRepository adRepository;

    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        ad.setId(null);
        return adRepository.save(ad);
    }

    @GetMapping("/search")
    public List<Ad> searchByPrice(@RequestParam Optional<Integer> minPrice, @RequestParam Optional<Integer> maxPrice) {
        int min = minPrice.orElse(0);
        int max = maxPrice.orElse(10_000_000);
        return adRepository.searchByPrice(min, max);
    }

    @PutMapping
    public Ad update(@RequestBody Ad updated) throws AccessDeniedException {
        return adRepository.updateAd(updated);
    }

    @GetMapping("/{tag}")
    public List<Ad> searchByTag(@PathVariable String tag) {
        return adRepository.searchByTag(tag);
    }
}
