/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order;
@org.apache.avro.specific.AvroGenerated
public enum RestaurantOrderStatus implements org.apache.avro.generic.GenericEnumSymbol<RestaurantOrderStatus> {
  PAID  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"RestaurantOrderStatus\",\"namespace\":\"net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order\",\"symbols\":[\"PAID\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
