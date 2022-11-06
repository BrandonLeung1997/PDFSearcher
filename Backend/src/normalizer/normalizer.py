from abc import ABC, abstractmethod


class Normalizer(ABC):
    @abstractmethod
    def _text_normalization(self):
        pass
