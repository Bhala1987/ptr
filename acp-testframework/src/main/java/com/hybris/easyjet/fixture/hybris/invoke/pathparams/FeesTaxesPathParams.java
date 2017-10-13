package com.hybris.easyjet.fixture.hybris.invoke.pathparams;


import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Builder
@EqualsAndHashCode(callSuper = false)

public class FeesTaxesPathParams extends PathParameters implements IPathParameters {

   private FeesTaxesPath path;
   private String feeTaxCode;

   @Override
   public String get() {
      if (!isPopulated(feeTaxCode)) {
         feeTaxCode = "";
      }

      if (path == null) {
         path = FeesTaxesPath.DEFAULT;
      }

      switch (path) {
         default:
            return feeTaxCode;
      }
   }

   public enum FeesTaxesPath {
      DEFAULT;
   }
}
