package rocks.milspecsg.msrepository.service.config.implementation;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;
import rocks.milspecsg.msrepository.service.config.ConfigKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MSConfigurationService extends ApiConfigurationService {

    @Inject
    public MSConfigurationService(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeTypeMap() {
        nodeTypeMap.put(ConfigKeys.PARTY_CREATE_COST_DOUBLE, TypeToken.of(Double.class));

        nodeTypeMap.put(ConfigKeys.SOME_LIST, new TypeToken<List<Integer>>() {});

        nodeTypeMap.put(ConfigKeys.SOME_MAP, new TypeToken<Map<String, Map<String, Integer>>>() {});
    }

    @Override
    protected void initVerificationMaps() {

    }

    @Override
    protected void initDefaultMaps() {

        defaultDoubleMap.put(ConfigKeys.PARTY_CREATE_COST_DOUBLE, 5.5d);

        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(7);
        list.add(9);
        defaultListMap.put(ConfigKeys.SOME_LIST, list);

        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> sub1 = new HashMap<>();
        sub1.put("example", 5);
        sub1.put("another", 6);
        Map<String, Integer> sub2 = new HashMap<>();
        sub2.put("ree", 2);
        sub2.put("car", 9);
        map.put("sub1", sub1);
        map.put("sub2", sub2);

        defaultMapMap.put(ConfigKeys.SOME_MAP, map);
    }

    @Override
    protected void initNodeNameMap() {

        nodeNameMap.put(ConfigKeys.PARTY_CREATE_COST_DOUBLE, "partyCreateCostDouble");

        nodeNameMap.put(ConfigKeys.SOME_LIST, "someList");

        nodeNameMap.put(ConfigKeys.SOME_MAP, "someMap");
    }

    @Override
    protected void initNodeDescriptionMap() {

        nodeDescriptionMap.put(ConfigKeys.PARTY_CREATE_COST_DOUBLE, "\nCost to create a party");

        nodeDescriptionMap.put(ConfigKeys.SOME_LIST, "\nSome random list");

        nodeDescriptionMap.put(ConfigKeys.SOME_MAP, "\nSome random map");
    }
}
