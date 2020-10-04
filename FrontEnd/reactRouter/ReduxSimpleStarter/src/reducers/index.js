import { combineReducers } from 'redux';
import PostsReducer from './reducer_posts';
import { reducer as formReducer } from 'redux-form';
const rootReducer = combineReducers({
  posts: PostsReducer,
  form: formReducer
}); // The form is a keyword for wiring up ReduxForm to ReduxState

export default rootReducer;
