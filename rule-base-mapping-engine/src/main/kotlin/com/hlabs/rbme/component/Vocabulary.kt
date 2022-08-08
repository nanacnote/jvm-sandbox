package com.hlabs.rbme.component

import com.hlabs.rbme.repository.RuleRepository
import org.springframework.stereotype.Component

@Component
class Vocabulary(
    private val repo: RuleRepository
) {
    private lateinit var graph: Trie

    fun expand() {
        graph = Trie()
        val rules = repo.findAll()
        for (rule in rules) {
            rule.fieldNames.forEach { graph.insert(it, rule.mapTo) }
        }
    }

    fun lookup(value: String): Set<String> {
        return graph.search(value)
    }

    private inner class TrieNode {
        var children: MutableMap<Char, TrieNode?> = HashMap()
        var endOfWord: Boolean = false
        var pointer =  mutableSetOf<String>()

    }
    private inner class Trie {
        private val root: TrieNode = TrieNode()

        fun insert(word: String, pointer: String) {
            var current = root
            for (element in word) {
                var node = current.children[element]
                if (node == null) {
                    node = TrieNode()
                    current.children[element] = node
                }
                current = node
            }
            //mark the current nodes endOfWord as true
            current.endOfWord = true
            current.pointer.add(pointer)
        }

        fun search(word: String): Set<String> {
            var current = root
            for (element in word) {
                val node = current.children[element] ?: return emptySet()
                current = node
            }
            return current.pointer
        }

        private fun delete(current: TrieNode = root, word: String, index: Int = 0): Boolean {
            if (index == word.length) {
                //when end of word is reached only delete if current.endOfWord is true.
                if (!current.endOfWord) {
                    return false
                }
                current.endOfWord = false
                //if current has no other mapping then return true
                return current.children.isEmpty()
            }
            val ch = word[index]
            val node = current.children[ch] ?: return false
            val shouldDeleteCurrentNode = delete(node, word, index + 1)

            //if true is returned then delete the mapping of character and trie-node reference from map.
            if (shouldDeleteCurrentNode) {
                current.children.remove(ch)
                //return true if no mappings are left in the map.
                return current.children.isEmpty()
            }
            return false
        }
    }
}