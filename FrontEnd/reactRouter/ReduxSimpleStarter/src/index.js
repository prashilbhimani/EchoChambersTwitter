import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import reducers from './reducers';
import PostsIndex from './components/posts_index'
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import promise from 'redux-promise'
import PostsNew from "./components/posts_new";

const createStoreWithMiddleware = applyMiddleware(promise)(createStore);


ReactDOM.render(
  <Provider store={createStoreWithMiddleware(reducers)}>
    <BrowserRouter>
      <div>
        <Switch> // this will match in order of what is there.
          <Route path="/posts/new" component={PostsNew}/>
          <Route path="/" component={PostsIndex}/>
        </Switch>
      </div>
      </BrowserRouter>
  </Provider>
  , document.querySelector('.container'));
