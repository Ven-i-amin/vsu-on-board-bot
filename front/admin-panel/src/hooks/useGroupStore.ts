import { useRef, useState } from 'react'
import {
  fetchGroupByName,
  fetchGroupChildren,
  fetchGroupQuestions,
  fetchRootGroup,
  type GroupNode,
} from '../api/adminApi'
import { Group as GroupModel, Question } from '../entities/models'

export type CachedGroupMap = Record<string, GroupNode>

export function toGroupModel(node: GroupNode | null, groupsByName: CachedGroupMap): GroupModel | null {
  if (!node) return null

  return new GroupModel({
    name: node.name,
    title: node.title,
    parentName: node.parentName,
    innerGroups: node.childrenNames
      .map((childName) => groupsByName[childName])
      .filter((child): child is GroupNode => Boolean(child))
      .map(
        (child) =>
          new GroupModel({
            name: child.name,
            title: child.title,
            parentName: child.parentName,
            innerGroups: [],
            questions: [],
          }),
      ),
    questions: node.questions.map((q) => new Question(q)),
  })
}

export function useGroupStore() {
  const [groupsByName, setGroupsByName] = useState<CachedGroupMap>({})
  const [rootGroupName, setRootGroupName] = useState<string | null>(null)
  const groupsByNameRef = useRef<CachedGroupMap>({})
  const rootGroupNameRef = useRef<string | null>(null)

  function syncRefs(groups: CachedGroupMap, root: string | null) {
    groupsByNameRef.current = groups
    rootGroupNameRef.current = root
  }

  function mergeGroups(groups: GroupNode[]) {
    setGroupsByName((current) => {
      const next = { ...current }
      for (const group of groups) {
        const existing = next[group.name]
        next[group.name] = {
          ...existing,
          ...group,
          title: group.title ?? existing?.title ?? {},
          parentName: group.parentName ?? existing?.parentName ?? '',
          parents: group.parents ?? existing?.parents ?? [],
          childrenNames: group.childrenNames.length > 0 ? group.childrenNames : (existing?.childrenNames ?? []),
          questions: group.questions.length > 0 ? group.questions : (existing?.questions ?? []),
          isLoaded: group.isLoaded ?? existing?.isLoaded ?? false,
        }
      }
      groupsByNameRef.current = next
      return next
    })
  }

  async function ensureGroupLoaded(groupName: string, options?: { force?: boolean }): Promise<GroupNode> {
    const force = options?.force ?? false
    const current = groupsByNameRef.current[groupName]

    if (current?.isLoaded && !force) {
      return current
    }

    const [group, children, questions] = await Promise.all([
      fetchGroupByName(groupName),
      fetchGroupChildren(groupName),
      fetchGroupQuestions(groupName),
    ])

    const loaded: GroupNode = {
      ...group,
      childrenNames: children.map((c) => c.name),
      questions,
      isLoaded: true,
    }

    mergeGroups([loaded, ...children])
    return loaded
  }

  async function ensureRootLoaded(): Promise<GroupNode> {
    const root = await fetchRootGroup()
    setRootGroupName(root.name)
    rootGroupNameRef.current = root.name

    const [children, questions] = await Promise.all([
      fetchGroupChildren(root.name),
      fetchGroupQuestions(root.name),
    ])

    const loaded: GroupNode = {
      ...root,
      childrenNames: children.map((c) => c.name),
      questions,
      isLoaded: true,
    }

    mergeGroups([loaded, ...children])
    return loaded
  }

  async function ensureAncestorsLoaded(parents: string[]) {
    for (const parentName of parents) {
      const existing = groupsByNameRef.current[parentName]
      if (!existing) {
        const parentGroup = await fetchGroupByName(parentName)
        mergeGroups([parentGroup])
      }
    }
  }

  function renameGroupInCache(previousName: string, nextGroup: GroupNode): void {
    setGroupsByName((current) => {
      const previousGroup = current[previousName]
      if (!previousGroup) return current

      const next: CachedGroupMap = {}
      for (const [name, group] of Object.entries(current)) {
        if (name === previousName) {
          next[nextGroup.name] = {
            ...previousGroup,
            ...nextGroup,
            childrenNames: previousGroup.childrenNames,
            questions: previousGroup.questions.map((q) => new Question({ ...q, parent: nextGroup.name })),
            isLoaded: previousGroup.isLoaded,
          }
        } else {
          next[name] = {
            ...group,
            parentName: group.parentName === previousName ? nextGroup.name : group.parentName,
            parents: group.parents.map((p) => (p === previousName ? nextGroup.name : p)),
            childrenNames: group.childrenNames.map((c) => (c === previousName ? nextGroup.name : c)),
            questions: group.questions.map((q) =>
              q.parent === previousName ? new Question({ ...q, parent: nextGroup.name }) : new Question(q),
            ),
          }
        }
      }

      groupsByNameRef.current = next
      return next
    })
  }

  function removeGroupFromCache(groupName: string): void {
    setGroupsByName((current) => {
      if (!current[groupName]) return current

      const namesToDelete = new Set<string>()
      const queue = [groupName]
      while (queue.length > 0) {
        const curr = queue.shift()!
        if (namesToDelete.has(curr)) continue
        namesToDelete.add(curr)
        for (const group of Object.values(current)) {
          if (group.parentName === curr) queue.push(group.name)
        }
      }

      const next: CachedGroupMap = {}
      for (const [name, group] of Object.entries(current)) {
        if (!namesToDelete.has(name)) {
          next[name] = {
            ...group,
            childrenNames: group.childrenNames.filter((c) => !namesToDelete.has(c)),
            questions: group.questions.map((q) => new Question(q)),
          }
        }
      }

      groupsByNameRef.current = next
      return next
    })
  }

  function removeQuestionFromCache(questionId: string): void {
    setGroupsByName((current) => {
      const next: CachedGroupMap = {}
      for (const [name, group] of Object.entries(current)) {
        next[name] = {
          ...group,
          questions: group.questions
            .filter((q) => q.questionId !== questionId)
            .map((q) => new Question(q)),
        }
      }
      groupsByNameRef.current = next
      return next
    })
  }

  function updateQuestionInCache(questionId: string, updatedQuestion: Question): void {
    setGroupsByName((current) => {
      const next: CachedGroupMap = {}
      for (const [name, group] of Object.entries(current)) {
        next[name] = {
          ...group,
          questions: group.questions.map((q) =>
            q.questionId === questionId ? new Question(updatedQuestion) : new Question(q),
          ),
        }
      }
      groupsByNameRef.current = next
      return next
    })
  }

  function addGroupToCache(parentName: string, newGroup: GroupNode): void {
    setGroupsByName((current) => {
      const parent = current[parentName]
      if (!parent) return current

      const next = {
        ...current,
        [newGroup.name]: newGroup,
        [parentName]: {
          ...parent,
          childrenNames: [...parent.childrenNames, newGroup.name],
        },
      }

      groupsByNameRef.current = next
      return next
    })
  }

  function addQuestionToCache(groupName: string, question: Question): void {
    setGroupsByName((current) => {
      const group = current[groupName]
      if (!group) return current

      const next = {
        ...current,
        [groupName]: {
          ...group,
          questions: [...group.questions, question],
        },
      }

      groupsByNameRef.current = next
      return next
    })
  }

  function replaceGroupQuestions(groupName: string, questions: Question[]): void {
    setGroupsByName((current) => {
      const group = current[groupName]
      if (!group) return current

      const next = {
        ...current,
        [groupName]: { ...group, questions, isLoaded: true },
      }

      groupsByNameRef.current = next
      return next
    })
  }

  function resetStore() {
    setGroupsByName({})
    setRootGroupName(null)
    groupsByNameRef.current = {}
    rootGroupNameRef.current = null
  }

  function getGroupsByName() {
    return groupsByNameRef.current
  }

  function getRootGroupName() {
    return rootGroupNameRef.current
  }

  function findQuestionById(questionId: string) {
    for (const group of Object.values(groupsByNameRef.current)) {
      const question = group.questions.find((q) => q.questionId === questionId)
      if (question) return { question: new Question(question), groupName: group.name }
    }
    return null
  }

  function findQuestionByName(groupName: string, questionName: string) {
    const group = groupsByNameRef.current[groupName]
    const question = group?.questions.find((q) => q.name === questionName)
    if (question) return { question: new Question(question), groupName }
    return null
  }

  return {
    groupsByName,
    rootGroupName,
    getGroupsByName,
    getRootGroupName,
    toGroupModel: (name: string | null) =>
      toGroupModel(name ? groupsByNameRef.current[name] ?? null : null, groupsByNameRef.current),
    ensureGroupLoaded,
    ensureRootLoaded,
    ensureAncestorsLoaded,
    mergeGroups,
    renameGroupInCache,
    removeGroupFromCache,
    removeQuestionFromCache,
    updateQuestionInCache,
    addGroupToCache,
    addQuestionToCache,
    replaceGroupQuestions,
    resetStore,
    findQuestionById,
    findQuestionByName,
    syncRefs,
  }
}
