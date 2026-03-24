package ru.vsu.core.util;

import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.dto.GroupTreeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupTreeUtil {
    public static GroupNodeDto findRootById(List<GroupNodeDto> nodes, String rootId) {
        return nodes.stream()
                .filter(node -> node.groupId().equals(rootId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static GroupNodeDto findRootByName(List<GroupNodeDto> nodes, String rootName) {
        return nodes.stream()
                .filter(node -> node.name().equals(rootName))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static GroupTreeDto buildTree(GroupNodeDto root, List<GroupNodeDto> nodes) {
        GroupTreeDto start = toTree(root);
        HashMap<String, GroupTreeDto> treeNodes = getNodesMap(start, root, nodes);

        fillInnerGroups(treeNodes, nodes);

        return start;
    }

    public static HashMap<String, GroupTreeDto> getNodesMap(GroupTreeDto startNode, GroupNodeDto rootNode, List<GroupNodeDto> nodes) {
        HashMap<String, GroupTreeDto> treeNodes = new HashMap<>();
        treeNodes.put(rootNode.name(), startNode);

        for (GroupNodeDto node : nodes) {
            GroupTreeDto tree = toTree(node);
            treeNodes.put(node.name(), tree);
        }

        return treeNodes;
    }

    public static void fillInnerGroups(HashMap<String, GroupTreeDto> treeNodes, List<GroupNodeDto> nodes) {
        for (GroupNodeDto node : nodes) {
            GroupTreeDto currentTree = treeNodes.get(node.name());
            GroupTreeDto parentTree = treeNodes.get(node.parentName());

            if (parentTree != null && currentTree != null) {
                parentTree.innerGroups().add(currentTree);
            }
        }
    }

    public static GroupTreeDto toTree(GroupNodeDto group) {
        return GroupTreeDto.builder()
                .groupId(group.groupId())
                .name(group.name())
                .title(group.title())
                .parentName(group.parentName())
                .innerGroups(new ArrayList<>())
                .questions(group.questions() == null ? new ArrayList<>() : group.questions())
                .build();
    }
}
