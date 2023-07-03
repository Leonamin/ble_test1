class ValueListener<T> {
  final Map<int, Function(T)> _listenerMap = <int, Function(T)>{};
  int _listenId = 0;
  int get currentListenerCnt => _listenerMap.length;

  void notify(T data) {
    for (Function(T) callback in _listenerMap.values) {
      callback.call(data);
    }
  }

  int add(Function(T) listener) {
    _listenId++;
    _listenerMap[_listenId] = listener;

    return _listenId;
  }

  void remove(int? listenId) {
    _listenerMap.remove(listenId);
  }

  void clear() {
    _listenerMap.clear();
  }
}
