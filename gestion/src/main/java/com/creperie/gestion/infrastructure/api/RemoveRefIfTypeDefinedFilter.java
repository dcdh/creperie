package com.creperie.gestion.infrastructure.api;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.media.Schema;

public class RemoveRefIfTypeDefinedFilter implements OASFilter {

    @Override
    public Schema filterSchema(final Schema schema) {
        // If the schema have a $ref and an explicit type defined
        if (schema.getRef() != null && schema.getType() != null) {
            // delete the ref to keep only the primitive type
            schema.setRef(null);
        }
        return schema;
    }
}
