package com.djsg38.locationprivacyapp.NearbyPlaces;

import com.google.android.gms.location.places.Place;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConvertPlaceTypes {
    private enum Types {
        accounting,
        administrative_area_level_1,
        administrative_area_level_2,
        administrative_area_level_3,
        airport,
        amusement_park,
        art_gallery,
        atm,
        bakery,
        bank,
        bar,
        beauty_salon,
        bicycle_store,
        book_store,
        bowling_alley,
        bus_station,
        cafe,
        campground,
        car_dealer,
        car_rental,
        car_wash,
        casino,
        cemetery,
        church,
        city_hall,
        clothing_store,
        colloquial_area,
        convenience_store,
        country,
        courthouse,
        dentist,
        department_store,
        doctor,
        electrician,
        electronics_store,
        embassy,
        establishment,
        finance,
        fire_station,
        floor,
        florist,
        food,
        funeral_home,
        gas_station,
        general_contractor,
        geocode,
        gym,
        hair_care,
        hardware_store,
        health,
        hindu_temple,
        home_goods_store,
        hospital,
        insurance_agency,
        intersection,
        jewelry_store,
        laundry,
        lawyer,
        library,
        liquor_store,
        local_government_office,
        locality,
        locksmith,
        lodging,
        meal_delivery,
        meal_takeaway,
        mosque,
        movie_rental,
        movie_theater,
        moving_company,
        museum,
        natural_feature,
        neighborhood,
        night_club,
        painter,
        park,
        parking,
        pet_store,
        pharmacy,
        physiotherapist,
        place_of_worship,
        plumber,
        police,
        political,
        point_of_interest,
        post_box,
        post_office,
        postal_code,
        postal_code_prefix,
        postal_town,
        premise,
        real_estate_agency,
        restaurant,
        roofing_contractor,
        roo,
        route,
        rv_park,
        school,
        shoe_store,
        shopping_mall,
        spa,
        stadium,
        storage,
        store,
        street_address,
        sublocality_level_1,
        sublocality_level_2,
        sublocality_level_3,
        sublocality_level4,
        sublocality_level_5,
        subpremise,
        subway_station,
        synagogue,
        taxi_stand,
        train_station,
        transit_station,
        travel_agency,
        university,
        veterinary_care,
        zoo
    }

    public List<String> getStringTypes(List<Integer> types) throws IllegalAccessException {
        List<String> typeStrings = new ArrayList<>();
        String name;

        for(Integer value : types) {
            name = getPlaceTypeForValue(value);
            // Remove types that are not helpful for identification
            if(name.equals("establishment")) {
                continue;
            }
            else if(name.equals("point_of_interest"))
                continue;
            else if(name.equals("postal_code"))
                continue;
            else if(name.equals("postal_code_prefix"))
                continue;
            else if(name.equals("postal_town"))
                continue;
            else if(name.equals("point_of_interest"))
                continue;
            else if(name.equals("street_address"))
                continue;

            typeStrings.add(name);
        }

        return typeStrings;
    }

    private String getPlaceTypeForValue(int value) throws IllegalAccessException {
        Field[] fields = Place.class.getDeclaredFields();
        String name;
        for(Field field : fields) {
            name = field.getName().toLowerCase();
            if(name.startsWith("type_") && field.getInt(null) == value) {
                return name.replace("type_", "");
            }
        }
        throw new IllegalArgumentException("place value " + value + " not found.");
    }
}