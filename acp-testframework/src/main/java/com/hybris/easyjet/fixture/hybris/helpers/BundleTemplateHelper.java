package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.dao.BundleTemplateDao;
import com.hybris.easyjet.database.hybris.dao.ProductDao;
import com.hybris.easyjet.database.hybris.models.BundleTemplateModel;
import com.hybris.easyjet.database.hybris.models.ProductModel;
import com.hybris.easyjet.fixture.hybris.helpers.dto.bundletemplate.BundleTemplateDTO;
import com.hybris.easyjet.fixture.hybris.helpers.dto.bundletemplate.ProductDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by marco on 23/02/17.
 */
@Component
public class BundleTemplateHelper {
    @Autowired
    private BundleTemplateDao bundleTemplateDao;
    @Autowired
    private ProductDao productDao;

    public List<BundleTemplateDTO> getStagedBundleTemplates() {
        List<BundleTemplateDTO> bundleTemplateDTOS = groupBundleTemplates(bundleTemplateDao.getStagedBundleTemplates());
        if (CollectionUtils.isNotEmpty(bundleTemplateDTOS)) {
            bundleTemplateDTOS.forEach(bt -> bt.setProducts(convertProductsToDTO(productDao.getProductsForBundle(bt.getId()))));
        }
        return bundleTemplateDTOS;
    }

    public List<BundleTemplateDTO> getStagedBundleTemplatesWithGdsFareClass(String gdsFareClass) {
        List<BundleTemplateDTO> bundleTemplateDTOS = groupBundleTemplates(
                bundleTemplateDao.getStagedBundleTemplatesWithGdsFareClass(gdsFareClass));
        if (CollectionUtils.isNotEmpty(bundleTemplateDTOS)) {
            bundleTemplateDTOS.forEach(bt -> bt.setProducts(convertProductsToDTO(productDao.getProductsForBundle(bt.getId()))));
        }
        return bundleTemplateDTOS;
    }

    public void removeProductTypeForBundle(BundleTemplateDTO bundleTemplateDTO, String productType) {
        bundleTemplateDTO.setProducts(bundleTemplateDTO.getProducts().stream()
                .filter(productDTO -> !productType.equalsIgnoreCase(productDTO.getProductType())).collect(Collectors.toList()));
    }

    private List<BundleTemplateDTO> groupBundleTemplates(List<BundleTemplateModel> models) {
        Map<String, BundleTemplateDTO> foundBundles = new HashMap<>();

        models.forEach(model -> {
            BundleTemplateDTO currentBundleTemplateDTO;
            if (foundBundles.containsKey(model.getId())) {
                currentBundleTemplateDTO = foundBundles.get(model.getId());
            } else {
                currentBundleTemplateDTO = new BundleTemplateDTO();
                currentBundleTemplateDTO.setId(model.getId());
                currentBundleTemplateDTO.setGdsFareClass(model.getGdsFareClass());
                currentBundleTemplateDTO.setDescriptions(new HashMap<>());
                currentBundleTemplateDTO.setFareconditions(new HashMap<>());
                currentBundleTemplateDTO.setNames(new HashMap<>());
                foundBundles.put(model.getId(), currentBundleTemplateDTO);
            }
            currentBundleTemplateDTO.getDescriptions().put(model.getLanguage(), model.getDescription());
            currentBundleTemplateDTO.getFareconditions().put(model.getLanguage(), model.getFareconditions());
            currentBundleTemplateDTO.getNames().put(model.getLanguage(), model.getName());
        });

        return foundBundles.values().stream().collect(Collectors.toList());
    }

    private List<ProductDTO> convertProductsToDTO(List<ProductModel> productsForBundle) {
        List<ProductDTO> products = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productsForBundle)) {
            productsForBundle.forEach(
                    prod -> products.add(ProductDTO.builder().code(prod.getCode()).productType(prod.getProductType()).build()));
        }
        return products;
    }

}
