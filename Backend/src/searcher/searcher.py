from abc import ABC, abstractmethod


class Searcher(ABC):
    @abstractmethod
    def search(self, document, index):
        pass

