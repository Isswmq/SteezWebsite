package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.website.steez.dto.product.DiscountCreateEditDto;
import org.website.steez.mapper.product.createEdit.DiscountCreateEditMapper;
import org.website.steez.model.product.Discount;
import org.website.steez.repository.DiscountRepository;
import org.website.steez.service.DiscountService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountCreateEditMapper createEditMapper;

    @Override
    public Optional<Discount> findById(Integer id) {
        return discountRepository.findById(id);
    }

    @Override
    public Optional<Discount> findByName(String name) {
        return discountRepository.findByName(name);
    }

    @Override
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public Discount create(DiscountCreateEditDto dto) {
        Discount entity = createEditMapper.toEntity(dto);
        return discountRepository.save(entity);
    }
}
