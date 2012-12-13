package com.service.solr;

import org.springframework.data.solr.core.query.Field;



	public enum SolrSearchableFields implements Field {

		TAG (SearchablePhoto.TAG_FIELD);

		private final String fieldName;

		private SolrSearchableFields(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String getName() {
			return fieldName;
		}

	}
	

