from .normalizer import Normalizer
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer


class QueryNormalizer(Normalizer):
    def __init__(self, keywords):
        self.__keywords = keywords
        self._text_normalization()

    def _text_normalization(self):
        stop_words = set(stopwords.words('english'))
        ps = PorterStemmer()
        self.__keywords = [
            {'keyword': ps.stem(data['keyword'].lower()), 'pos': data['pos'], 'operator': data['operator']}
            for data in self.__keywords if not (data['keyword'].lower() in stop_words)]

    def get_query(self):
        return self.__keywords
